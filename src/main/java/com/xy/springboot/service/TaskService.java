package com.xy.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.springboot.common.DeleteRequest;
import com.xy.springboot.model.dto.task.TaskCreateRequest;
import com.xy.springboot.model.dto.task.TaskQueryRequest;
import com.xy.springboot.model.entity.Task;
import com.xy.springboot.model.vo.TaskVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* 任务服务
*/
public interface TaskService extends IService<Task> {
    /**
     * 任务创建
     *
     * @param request           用户 id
     * @param taskCreateRequest
     * @return 新任务 id
     */
    Long taskCreate(HttpServletRequest request, TaskCreateRequest taskCreateRequest);

    /**
     * 任务更新
     *
     * @param taskId    任务 id
     * @param taskStatus 任务状态
     * @return
     */
    boolean taskUpdate(Long taskId, String taskStatus);

    /**
     * 任务删除
     *
     * @param deleteRequest 删除请求
     * @param request 请求
     * @return
     */
    boolean taskDelete(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 任务视图转化
     *
     * @param task 任务
     * @return
     */
    public TaskVO convertTaskVO(Task task);

    /**
     * 列表任务视图转化
     *
     * @param tasks 任务列表
     * @return
     */
    public List<TaskVO> convertTaskVOList(List<Task> tasks);

    /**
     * 获取任务查询条件
     *
     * @param taskQueryRequest 任务查询请求
     * @param request          请求
     * @return
     */
    QueryWrapper<Task> getTaskQueryWrapper(TaskQueryRequest taskQueryRequest, HttpServletRequest request);

    /**
     * 内部查询待完成任务，一次查询 5 条
     *
     * @return
     */
    List<Task> listUnfinishedTask();
}
