package com.xy.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xy.springboot.common.BaseResponse;
import com.xy.springboot.common.DeleteRequest;
import com.xy.springboot.common.ResultUtils;
import com.xy.springboot.model.dto.task.TaskCreateRequest;
import com.xy.springboot.model.dto.task.TaskQueryRequest;
import com.xy.springboot.model.entity.Task;
import com.xy.springboot.model.entity.User;
import com.xy.springboot.model.vo.TaskVO;
import com.xy.springboot.service.TaskService;
import com.xy.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 创建新任务
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTask(@RequestBody TaskCreateRequest taskCreateRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long taskId = taskService.taskCreate(request, taskCreateRequest);
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
