package com.quicktvui.sdk.core.utils;

import java.util.Hashtable;
import java.util.Map;

public class TaskProgressManager {
    private final Map<String, Double> taskProgressMap;
    private final Map<String, Integer> taskWeights;
    private TaskProgressCallback mTaskProgressCallBack;

    private static TaskProgressManager instance;

    public static TaskProgressManager getInstance() {
        if (instance == null) {
            instance = new TaskProgressManager();
        }
        return instance;
    }

    private TaskProgressManager() {
        this.taskProgressMap = new Hashtable<>();
        this.taskWeights = new Hashtable<>();
    }

    /**
     * 添加任务
     *
     * @param taskId 任务ID
     * @param weight 进度占比（总占比：100）
     */
    public void addTask(String taskId, int weight) {
        taskProgressMap.put(taskId, 0.0);
        taskWeights.put(taskId, weight);
    }

    /**
     * 更新任务进度
     *
     * @param taskId   任务ID
     * @param progress 执行进度（0-100）
     */
    public void updateTaskProgress(String taskId, double progress) {
        if (taskProgressMap.containsKey(taskId)) {
            taskProgressMap.put(taskId, progress);
            updateProgress();
        }
    }

    /**
     * 移除任务
     *
     * @param taskId 任务ID
     */
    public void removeTask(String taskId) {
        taskProgressMap.remove(taskId);
        taskWeights.remove(taskId);
        updateProgress();
    }

    /**
     * 重置所有任务进度
     */
    public void reset() {
        taskProgressMap.clear();
        taskWeights.clear();
        updateProgress();
    }

    /**
     * 更新总体进度
     */
    private void updateProgress() {
        int totalWeight = 0;
        double weightedProgressSum = 0.0;

        for (String taskId : taskProgressMap.keySet()) {
            int weight = taskWeights.get(taskId);
            double progress = taskProgressMap.get(taskId);

            totalWeight += weight;
            weightedProgressSum += (progress * weight);
        }

        double overallProgress = totalWeight == 0 ? 0.0 : weightedProgressSum / totalWeight;

        if (mTaskProgressCallBack != null) {
            mTaskProgressCallBack.onTaskProgressUpdate(overallProgress);
        }
    }

    /**
     * 设置进度回调
     */
    public void setTaskProgressCallback(TaskProgressCallback callback) {
        this.mTaskProgressCallBack = callback;
    }

    /**
     * 进度更新回调接口
     */
    public interface TaskProgressCallback {
        void onTaskProgressUpdate(double progress);
    }
}
