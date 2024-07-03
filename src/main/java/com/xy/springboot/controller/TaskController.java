package com.xy.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xy.springboot.boot.Launch;
import com.xy.springboot.common.BaseResponse;
import com.xy.springboot.common.DeleteRequest;
import com.xy.springboot.common.ErrorCode;
import com.xy.springboot.common.ResultUtils;
import com.xy.springboot.exception.BusinessException;
import com.xy.springboot.model.dto.task.*;
import com.xy.springboot.model.dto.task.config.ConditionCreateOrUpdateRequest;
import com.xy.springboot.model.dto.task.config.UncertaintyCreateOrUpdateRequest;
import com.xy.springboot.model.dto.task.config.VulnerabilityCreateOrUpdateRequest;
import com.xy.springboot.model.entity.*;
import com.xy.springboot.model.vo.TaskVO;
import com.xy.springboot.service.*;
import com.xy.springboot.utils.FolderToZipUtil;
import com.xy.springboot.utils.TaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 任务接口
 */

@RestController
@RequestMapping("/task")
@Slf4j
public class TaskController {

    @Value("${file.upload.url}")
    private String fileUploadUrl;

    @Resource
    private TaskService taskService;

    @Resource
    private UserService userService;

    @Resource
    private UncertaintyService uncertaintyService;

    @Resource
    private ConditionService conditionService;

    @Resource
    private VulnerabilityService vulnerabilityService;

    @Resource
    private Launch launch;

    /**
     * 创建新任务
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTask(@RequestBody TaskCreateRequest taskCreateRequest, HttpServletRequest request) {
        Long taskId = taskService.taskCreate(request, taskCreateRequest);
        return ResultUtils.success(taskId);
    }

    /**
     * 创建 or 更新 Uncertainty 任务配置
     * @param uncertaintyCreateOrUpdateRequest uncertaintyCreateOrUpdateRequest
     * @param request request
     * @return BaseResponse
     */
    @PostMapping("/config/Uncertainty")
    public BaseResponse<String> configUncertainty(@RequestBody UncertaintyCreateOrUpdateRequest uncertaintyCreateOrUpdateRequest,
                                                  HttpServletRequest request) {
        Long taskId = uncertaintyCreateOrUpdateRequest.getTaskId();
        User user = userService.getLoginUser(request);
        Task task = checkUserAuth(taskId, user);
        // 不确定性更新后，任务状态只能为 1，状态为 todo
        task.setCurStage(1);
        task.setConfig(task.getConfig() | 1);
        task.setCurStatus("todo");
        taskService.updateById(task);

        Uncertainty uncertainty = new Uncertainty();
        BeanUtils.copyProperties(uncertaintyCreateOrUpdateRequest, uncertainty);

        int changeNum = uncertaintyService.createOrUpdateUncertainty(uncertainty);
        if (changeNum == 0) {
            log.info("不确定性配置更新失败");
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "不确定性配置更新失败");
        }
        return ResultUtils.success("success");
    }

    /**
     * 创建 or 更新 Condition 任务配置
     * @param conditionCreateOrUpdateRequest conditionCreateOrUpdateRequest
     * @param request request
     * @return BaseResponse
     */
    @PostMapping("/config/Condition")
    public BaseResponse<String> configCondition(@RequestBody ConditionCreateOrUpdateRequest conditionCreateOrUpdateRequest,
                                                HttpServletRequest request) {
        Long taskId = conditionCreateOrUpdateRequest.getTaskId();
        User user = userService.getLoginUser(request);
        Task task = checkUserAuth(taskId, user);
        // 运行工况更新后，任务状态最大只能为 2，状态为 todo
        task.setConfig(task.getConfig() | 2);
        if (task.getCurStage() >= 2) {
            launch.terminatePythonTask(taskId);
            task.setCurStage(2);
            task.setCurStatus("todo");
        } else if (task.getCurStage() == 1 && "done".equals(task.getCurStatus())) {
            task.setCurStage(2);
        }
        taskService.updateById(task);

        Condition condition = new Condition();
        BeanUtils.copyProperties(conditionCreateOrUpdateRequest, condition);
        int changeNum = conditionService.createOrUpdateCondition(condition);
        if (changeNum == 0) {
            log.info("运行工况配置更新失败");
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "运行工况配置更新失败");
        }
        return ResultUtils.success("success");
    }

    /**
     * 创建 or 更新 Vulnerability 任务配置
     * @param vulnerabilityCreateOrUpdateRequest vulnerabilityCreateOrUpdateRequest
     * @param request request
     * @return BaseResponse
     */
    @PostMapping("/config/Vulnerability")
    public BaseResponse<String> configVulnerability(@RequestBody VulnerabilityCreateOrUpdateRequest vulnerabilityCreateOrUpdateRequest,
                                                HttpServletRequest request) {
        Long taskId = vulnerabilityCreateOrUpdateRequest.getTaskId();
        User user = userService.getLoginUser(request);
        Task task = checkUserAuth(taskId, user);
        // 脆弱性更新后，任务状态最大只能为 3，状态为 todo
        task.setConfig(task.getConfig() | 4);
        if (task.getCurStage() >= 3) {
            launch.terminatePythonTask(taskId);
            task.setCurStage(3);
            task.setCurStatus("todo");
        } else if (task.getCurStage() == 2 && "done".equals(task.getCurStatus())) {
            task.setCurStage(3);
        }
        taskService.updateById(task);

        Vulnerability vulnerability = new Vulnerability();
        BeanUtils.copyProperties(vulnerabilityCreateOrUpdateRequest, vulnerability);
        int changeNum = vulnerabilityService.createOrUpdateVulnerability(vulnerability);
        if (changeNum == 0) {
            log.info("脆弱性配置更新失败");
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "脆弱性配置更新失败");
        }
        return ResultUtils.success("success");
    }

    /**
     * 下载任务结果
     * @param taskIdStr 任务id
     * @param taskStageStr 任务阶段
     * @param request 请求
     * @param response 响应
     */
    @GetMapping("/download")
    public void downloadResult(@RequestParam("taskId") String taskIdStr, @RequestParam("TaskStage") String taskStageStr
                                            , HttpServletRequest request, HttpServletResponse response) {
        User user = userService.getLoginUser(request);
        Long taskId = Long.parseLong(taskIdStr);
        Integer taskStage = Integer.parseInt(taskStageStr);
        Task task = checkUserAuth(taskId, user);

        // 1.检查任务是否完成
        if (task.getCurStage() < taskStage) {
            log.info("任务 {} 未开始", taskId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务未开始");
        } else if (task.getCurStage().equals(taskStage) && !"done".equals(task.getCurStatus())) {
            log.info("任务 {} 未完成", taskId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务未完成");
        }
        // 2. 压缩文件夹并下载
        FolderToZipUtil.zip(TaskUtils.getOutputDirByStage(taskId, taskStage), response);
    }

    /**
     * 任务执行
     * @param taskRunRequest 任务执行请求
     * @param request 请求
     * @return 是否执行成功
     */
    @PostMapping("/run")
    public BaseResponse<String> runTask(@RequestBody TaskRunRequest taskRunRequest, HttpServletRequest request) {
        Long taskId = taskRunRequest.getTaskId();
        User user = userService.getLoginUser(request);
        Task task = checkUserAuth(taskId, user);
        Integer taskStage = taskRunRequest.getTaskStage();
        // 1.检查任务是否完成
        if (task.getCurStage() < taskStage - 1) {
            log.info("任务 {} 阶段 {} 无法开始", taskId, taskStage);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务无法开始");
        } else if (task.getCurStage().equals(taskStage - 1) && !"done".equals(task.getCurStatus())) {
            log.info("任务 {} 阶段 {} 无法完成", taskId, taskStage);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务无法执行");
        }
        // 2. 配置文件是否到位
        if (taskStage == 1 && (task.getConfig() & 1) == 0) {
            log.info("任务 {} 阶段 {} 配置文件不完整", taskId, taskStage);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "配置文件不完整");
        } else if (taskStage == 2 && (task.getConfig() & 3) == 0) {
            log.info("任务 {} 阶段 {} 配置文件不完整", taskId, taskStage);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "配置文件不完整");
        } else if (taskStage >= 3 && (task.getConfig() & 7) == 0) {
            log.info("任务 {} 阶段 {} 配置文件不完整", taskId, taskStage);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "配置文件不完整");
        }
        // 3.任务执行
        launch.executePythonTask(taskId, taskStage);
        return ResultUtils.success("任务执行中");
    }

    /**
     * 取消任务
     * @param cancelRequest 取消请求
     * @param request 请求
     * @return 是否取消成功
     */
    @PostMapping("/cancel")
    public BaseResponse<String> cancelTask(@RequestBody TaskCancelRequest cancelRequest, HttpServletRequest request) {
        Long taskId = cancelRequest.getTaskId();
        User user = userService.getLoginUser(request);
        Task task = checkUserAuth(taskId, user);
        // 1.任务取消
        launch.terminatePythonTask(taskId);
        return ResultUtils.success("任务取消成功");
    }


    /**
     * 检查是否有权限操作任务
     * @param taskId 任务id
     * @param user  用户
     */
    public Task checkUserAuth(Long taskId, User user) {
        Task task = taskService.getById(taskId);
        if (task == null) {
            log.info("任务 {} 不存在", taskId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务不存在");
        } else if (!task.getUserId().equals(user.getId())) {
            log.info("用户 {} 无权操作任务 {}", user.getId(), taskId);
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权操作任务");
        }

        return task;
    }

    /**
     * 删除任务
     *
     * @param deleteRequest 删除请求
     * @param request 请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTask(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        boolean result = taskService.taskDelete(deleteRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 多条件查询任务
     *
     * @param taskQueryRequest 任务查询请求
     * @param request          请求
     * @return 任务列表
     */
    @PostMapping("/query")
    public BaseResponse<List<TaskVO>> queryTask(@RequestBody TaskQueryRequest taskQueryRequest,
                                                HttpServletRequest request) {
        QueryWrapper<Task> queryWrapper = taskService.getTaskQueryWrapper(taskQueryRequest, request);
        List<Task> taskList = taskService.list(queryWrapper);
        return ResultUtils.success(taskService.convertTaskVOList(taskList));
    }
}
