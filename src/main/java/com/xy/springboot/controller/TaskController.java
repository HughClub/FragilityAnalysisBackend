package com.xy.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xy.springboot.common.BaseResponse;
import com.xy.springboot.common.ErrorCode;
import com.xy.springboot.common.ResultUtils;
import com.xy.springboot.exception.BusinessException;
import com.xy.springboot.model.dto.task.TaskQueryRequest;
import com.xy.springboot.model.entity.Task;
import com.xy.springboot.model.entity.User;
import com.xy.springboot.service.TaskService;
import com.xy.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

/**
 * 任务接口
 */

@RestController
@RequestMapping("/task")
@Slf4j
public class TaskController {

    @Value("${file.upload.url}")
    private  String fileUploadUrl;

    @Resource
    private TaskService taskService;

    @Resource
    private UserService userService;

    /**
     * 创建新任务
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTask(@RequestPart("files")MultipartFile[] files, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long taskId = taskService.taskCreate(loginUser.getId());
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();  // 文件名
            File dest = new File(fileUploadUrl + fileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try {
                file.transferTo(dest);
            } catch (Exception e) {
                log.error("文件上传失败", e);
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        return ResultUtils.success(taskId);
    }

    /**
     * 删除任务
     *
     * @param taskId 任务 id
     * @param request 请求
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTask(@RequestParam Long taskId, HttpServletRequest request) {
        boolean result = taskService.taskDelete(taskId, request);
        return ResultUtils.success(result);
    }

    /**
     * 多条件查询任务
     *
     * @param taskQueryRequest 任务查询请求
     * @return
     */
    @PostMapping("/query")
    public BaseResponse<List<Task>> queryTask(@RequestBody TaskQueryRequest taskQueryRequest) {
        QueryWrapper<Task> queryWrapper = taskService.getTaskQueryWrapper(taskQueryRequest);
        List<Task> taskList = taskService.list(queryWrapper);
        return ResultUtils.success(taskList);
    }
}
