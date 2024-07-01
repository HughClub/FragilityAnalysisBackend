package com.xy.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.springboot.common.DeleteRequest;
import com.xy.springboot.common.ErrorCode;
import com.xy.springboot.exception.BusinessException;
import com.xy.springboot.model.dto.task.TaskQueryRequest;
import com.xy.springboot.model.entity.Task;
import com.xy.springboot.mapper.TaskMapper;
import com.xy.springboot.model.entity.User;
import com.xy.springboot.model.vo.TaskVO;
import com.xy.springboot.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        task.setCurStatus(taskStatus);
        boolean result = this.updateById(task);
        if (!result) {
            log.info("任务 {} 更新失败", taskId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新任务失败");
        }
        log.info("任务 {} 更新成功", taskId);
        return true;
    }

    @Override
    public boolean taskDelete(DeleteRequest deleteRequest, HttpServletRequest request) {
        Long taskId = deleteRequest.getId();
        // 1.任务是否存在
        Task task = this.getById(taskId);
        if (task == null) {
            log.info("任务 {} 不存在", taskId);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "任务不存在");
        }
        // 2.用户是否有权删除任务
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            log.info("用户未登录");
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
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
    public TaskVO convertTaskVO(Task task) {
        if (task == null) {
            return null;
        }
        TaskVO taskVO = new TaskVO();
        BeanUtils.copyProperties(task, taskVO);
        taskVO.setTaskId(task.getId());
        return taskVO;
    }

    @Override
    public List<TaskVO> convertTaskVOList(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return new ArrayList<>();
        }
        return tasks.stream().map(this::convertTaskVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<Task> getTaskQueryWrapper(TaskQueryRequest taskQueryRequest, HttpServletRequest request) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        if (taskQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        String taskStatus = taskQueryRequest.getTaskStatus();
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        queryWrapper.eq("userId", user.getId())
                .eq(StringUtils.isNotBlank(taskStatus), "taskStatus", taskStatus);
        return queryWrapper;
    }

    @Override
    public List<Task> listUnfinishedTask() {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("taskStatus", "todo");
        queryWrapper.last("limit 5");
        return this.list(queryWrapper);
    }
}
