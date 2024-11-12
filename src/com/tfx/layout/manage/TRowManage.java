package com.tfx.layout.manage;

import com.tfx.layout.view.TRow;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author tianfx
 * @date 2023/5/31 11:17
 */
public class TRowManage {

    private static final List<TRow> ALL = new ArrayList<>();
    
    public static int getY(Container container,TRow currTRow){
        int y = 0;
        for (TRow tRow : ALL) {
            if (Objects.equals(currTRow,tRow)){
                break;
            }
            if (Objects.equals(tRow.getSupperContainer(),container)){
                y+=tRow.getStheight();
            }
        }
        return y;
    }

    public static void register(TRow tRow){
        ALL.add(tRow);
    }
}
