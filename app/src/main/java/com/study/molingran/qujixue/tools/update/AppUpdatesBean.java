package com.study.molingran.qujixue.tools.update;

import java.util.List;

/**
 * @author Molingran
 * @create 2019/02/11 13:49
 */
public class AppUpdatesBean {

    /**
     * version : 0.2.1
     * content : 检查更新
     * date : 2019-02-11
     * link : http://www.yeek.top/Download/Android/getApp/qujixue/
     * future : [{"version":"0.2.2","content":"自动检查更新并在\u201c我的\u201d中提醒。"},{"version":"0.2.3","content":"App内置更新"}]
     */

    private String version;
    private String content;
    private String date;
    private String link;
    private List<FutureBean> future;
    /**
     * versionCode : 19021300
     */

    private String versionCode;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<FutureBean> getFuture() {
        return future;
    }

    public void setFuture(List<FutureBean> future) {
        this.future = future;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public static class FutureBean {
        /**
         * version : 0.2.2
         * content : 自动检查更新并在“我的”中提醒。
         */

        private String version;
        private String content;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
