package com.tfx.layout.view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * 分段式、共16段
 * @author tianfx
 * @date 2023/5/30 14:27
 */
public class TCol extends JPanel{
    
    private TRow tRow;
    private int span = 16;
    
    public static TCol create(TRow tRow,int span){
        if (span<0){
            span = 0;
        }
        if (span>TRow.ROWSPAN){
            span = TRow.ROWSPAN;
        }
        TCol tCol = new TCol();
        tCol.tRow = tRow;
        tCol.span = span;
        tCol.setBorder(new LineBorder(Color.red));
        return tCol;
    }

    public int getSpan() {
        return span;
    }
}
