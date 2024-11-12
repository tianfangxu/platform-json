package com.tfx.layout.manage;

//import robotMemory.view.TDialog;

import com.tfx.layout.view.TDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tianfx
 * @date 2023/5/30 10:51
 */
public class DialogManage {

    private static final List<TDialog> ALL = new ArrayList<>();
    
    public static void register(TDialog dialog){
        int index = 0;
        for (int i = 0; i < ALL.size(); i++) {
            if (ALL.get(i).getZindex() == dialog.getZindex()){
                index = i;break;
            }else if (ALL.get(i).getZindex() < dialog.getZindex()){
                index = i;
            }else{
                break;
            }
        }
        ALL.add(index,dialog);
    }
    
    public static int nextIndex(){
        int index = 0;
        if (ALL.size() > 0){
            index = ALL.get(ALL.size()-1).getZindex()+1;
        }
        return index;
    }
}
