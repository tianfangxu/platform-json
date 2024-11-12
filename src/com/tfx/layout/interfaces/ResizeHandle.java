package com.tfx.layout.interfaces;

/**
 * @author tianfx
 * @date 2023/5/30 13:48
 */
@FunctionalInterface
public interface ResizeHandle {

    /**
     * @param x 整体窗口的宽度
     * @param y 整体窗口的高度
     */
    public void resizeFun(int x,int y);
}
