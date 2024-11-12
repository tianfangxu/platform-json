package com.tfx.layout.manage;

import com.tfx.layout.interfaces.ResizeHandle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tianfx
 * @date 2023/5/30 13:51
 */
public class ResizeManage {
    private static final List<ResizeHandle> ALL = new ArrayList<>();

    public static void register(ResizeHandle handle){
        ALL.add(handle);
    }

    public static void resize(int x,int y){
        for (ResizeHandle resizeHandle : ALL) {
            resizeHandle.resizeFun(x,y);
        }
    }
}
