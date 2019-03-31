package com.study.molingran.qujixue.tools.download;

/**
 * @author Molingran
 * @create 2019/02/16 23:45
 *
 * 下载监听器
 */
public interface DownloadListener {
    /**
     * 开始
     */
    void onStart();

    /**
     * 进行百分百
     * @param currentLength 进度（百分制）
     */
    void onProgress(int currentLength);

    /**
     * 下载成功
     * @param localPath 文件路径
     */
    void onFinish(String localPath);

    /**
     * 文件已存在
     * @param localPath 文件路径
     */
    void onPresence(String localPath);

    /**
     * 下载失败
     * @param error 错误信息
     */
    void onFailure(String error);
}
