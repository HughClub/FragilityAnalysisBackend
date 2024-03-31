package com.xy.springboot.service.impl;

import cn.hutool.http.HttpRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.springboot.common.ErrorCode;
import com.xy.springboot.exception.BusinessException;
import com.xy.springboot.model.dto.task.TaskQueryRequest;
import com.xy.springboot.model.entity.Task;
import com.xy.springboot.mapper.TaskMapper;
import com.xy.springboot.model.entity.User;
import com.xy.springboot.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static com.xy.springboot.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 服务接口实现
 */
@Service
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
        implements TaskService {

    @Override
    public Long taskCreate(Long userId) {
        // 1. 创建任务
        Task task = new Task();
        task.setUserId(userId);
        boolean result = this.save(task);
        if (!result) {
            log.info("用户 {} 创建任务失败", userId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建任务失败");
        }
        log.info("用户 {} 创建任务 {}", userId, task.getId());
        return task.getId();
    }

    @Override
    public boolean taskUpdate(Long taskId, String taskStatus) {
        // 1. 更新任务
        Task task = new Task();
        task.setId(taskId);
        task.setTaskStatus(taskStatus);
        boolean result = this.updateById(task);
        if (!result) {
            log.info("任务 {} 更新失败", taskId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新任务失败");
        }
        log.info("任务 {} 更新成功", taskId);
        return true;
    }

    @Override
    public boolean taskDelete(Long taskId, HttpServletRequest request) {
        // 1.任务是否存在
        Task task = this.getById(taskId);
        if (task == null) {
            log.info("任务 {} 不存在", taskId);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "任务不存在");
        }
        // 2.用户是否有权删除任务
        User user = (User) request.getAttribute(USER_LOGIN_STATE);
        if (!task.getUserId().equals(user.getId())) {
            log.info("用户 {} 无权删除任务 {}", user.getId(), taskId);
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权删除任务");
        }
        // 3. 删除任务
        boolean result = this.removeById(taskId);
        if (!result) {
            log.info("任务 {} 删除失败", taskId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除任务失败");
        }
        return true;
    }

    @Override
    public QueryWrapper<Task> getTaskQueryWrapper(TaskQueryRequest taskQueryRequest) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        if (taskQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (taskQueryRequest.getTaskId() != null) {
            queryWrapper.eq("id", taskQueryRequest.getTaskId());
        }
        if (taskQueryRequest.getUserId() != null) {
            queryWrapper.eq("user_id", taskQueryRequest.getUserId());
        }
        if (taskQueryRequest.getTaskStatus() != null) {
            queryWrapper.eq("task_status", taskQueryRequest.getTaskStatus());
        }
        return queryWrapper;
    }
}
