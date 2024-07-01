package com.xy.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xy.springboot.common.BaseResponse;
import com.xy.springboot.common.DeleteRequest;
import com.xy.springboot.common.ErrorCode;
import com.xy.springboot.common.ResultUtils;
import com.xy.springboot.exception.BusinessException;
import com.xy.springboot.model.dto.task.TaskQueryRequest;
import com.xy.springboot.model.entity.Uncertainty;
import com.xy.springboot.model.entity.Task;
import com.xy.springboot.model.entity.User;
import com.xy.springboot.model.vo.TaskVO;
import com.xy.springboot.service.TaskService;
import com.xy.springboot.service.UserService;
import com.xy.springboot.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
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

    /**
     * 创建新任务
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTask(@RequestPart("files") MultipartFile[] files, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long taskId = taskService.taskCreate(loginUser.getId());
        // 1. 根据任务创建任务目录，下有两个目录，input 目录存放输入文件，output目录存放输出文件（这里只创建出来）
        File inputDir = new File(fileUploadUrl + taskId + "/input");
        File outputDir = new File(fileUploadUrl + taskId + "/output");
        if (!inputDir.exists()) {
            inputDir.mkdirs();
        }
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        // 2. 保存文件
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();  // 文件名
            File dest = new File(fileUploadUrl + taskId + "/input/" + fileName);
            try {
                file.transferTo(dest);
            } catch (Exception e) {
//                log.error("文件上传失败", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
            }
            log.info("文件上传成功，保存到：{}", dest.getAbsolutePath());
        }
        return ResultUtils.success(taskId);
    }

    /**
     * 用参数创建新任务
     */
    @PostMapping("/create")
    public BaseResponse<Long> addTask(@RequestBody Uncertainty config, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long taskId = taskService.taskCreate(loginUser.getId());
        // 1. 根据任务创建任务目录，下有两个目录，input 目录存放输入文件，output目录存放输出文件（这里只创建出来）
        File inputDir = new File(fileUploadUrl + taskId + "/input");
        File outputDir = new File(fileUploadUrl + taskId + "/output");
        if (!inputDir.exists()) {
            inputDir.mkdirs();
        }
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        // 2. 保存文件
        try {
            ConfigUtils.generateConfigFile(taskId, config);
        } catch (IOException e) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
        return ResultUtils.success(taskId);
    }


    /**
     * 删除任务
     *
     * @param deleteRequest 删除请求
     * @param request 请求
     * @return
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
     * @return
     */
    @PostMapping("/query")
    public BaseResponse<List<TaskVO>> queryTask(@RequestBody TaskQueryRequest taskQueryRequest,
                                                HttpServletRequest request) {
        QueryWrapper<Task> queryWrapper = taskService.getTaskQueryWrapper(taskQueryRequest, request);
        List<Task> taskList = taskService.list(queryWrapper);
        return ResultUtils.success(taskService.convertTaskVOList(taskList));
    }
}
