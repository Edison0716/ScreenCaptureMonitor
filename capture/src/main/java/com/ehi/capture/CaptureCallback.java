package com.ehi.capture;

/**
 * @author  EdisonLi
 * @version 1.0
 * @since 2020/12/24 16:46
 * desc : 捕捉到截屏文件回调
 */
public interface CaptureCallback {
    void capture(String imagePath);
}
