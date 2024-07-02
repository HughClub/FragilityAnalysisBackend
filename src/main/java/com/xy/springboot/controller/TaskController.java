package com.xy.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xy.springboot.common.BaseResponse;
import com.xy.springboot.common.DeleteRequest;
import com.xy.springboot.common.ErrorCode;
import com.xy.springboot.common.ResultUtils;
import com.xy.springboot.exception.BusinessException;
import com.xy.springboot.model.dto.task.TaskCreateRequest;
import com.xy.springboot.model.dto.task.TaskQueryRequest;
import com.xy.springboot.model.dto.task.config.ConditionCreateOrUpdateRequest;
import com.xy.springboot.model.dto.task.config.UncertaintyCreateOrUpdateRequest;
import com.xy.springboot.model.dto.task.config.VulnerabilityCreateOrUpdateRequest;
import com.xy.springboot.model.entity.*;
import com.xy.springboot.model.vo.TaskVO;
import com.xy.springboot.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
        Task task = checkLoginAuth(taskId, user);
        // 不确定性更新后，任务状态只能为 1，状态为 todo
        task.setCurStage(1);
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
        Task task = checkLoginAuth(taskId, user);
        // 运行工况更新后，任务状态只能为 2，状态为 todo
        task.setCurStage(2);
        task.setCurStatus("todo");
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

    public BaseResponse<String> configVulnerability(@RequestBody VulnerabilityCreateOrUpdateRequest vulnerabilityCreateOrUpdateRequest,
                                                HttpServletRequest request) {
        Long taskId = vulnerabilityCreateOrUpdateRequest.getTaskId();
        User user = userService.getLoginUser(request);
        Task task = checkLoginAuth(taskId, user);
        // 脆弱性更新后，任务状态只能为 3，状态为 todo
        task.setCurStage(3);
        task.setCurStatus("todo");
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

    // TODO 执行任务线程池
    // TODO 任务执行状态更新
    // TODO 任务执行结果保存
    // TODO 任务执行结果下载
    // TODO 任务执行取消



    /**
     * 检查是否有权限操作任务
     * @param taskId 任务id
     * @param user  用户
     */
    public Task checkLoginAuth(Long taskId, User user) {
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
