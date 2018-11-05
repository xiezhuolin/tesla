package cn.acewill.pos.next.utils;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import cn.acewill.pos.R;

/**
 * 自定义键盘
 * Created by aqw on 2016/9/9.
 */
public class KeyBoardUtil {

    private Context ctx;
    private KeyboardView keyboardView;
    private Keyboard k1;// 数字键盘
    private Keyboard k2;// 字母键盘
    public boolean isnun = true;// 是否数据键盘
    public boolean isupper = false;// 是否大写

    private EditText ed;

    public KeyBoardUtil(Context ctx,KeyboardView keyboardView, EditText edit) {
        this.ctx = ctx;
        this.ed = edit;
        int inType = edit.getInputType();

        k1 = new Keyboard(ctx, R.xml.key_number);
        k2 = new Keyboard(ctx, R.xml.key_letter);

        this.keyboardView = keyboardView;

        if(inType==InputType.TYPE_CLASS_NUMBER){//数字键盘
            isnun = true;
            keyboardView.setKeyboard(k1);
        }else {
            isnun = false;
            keyboardView.setKeyboard(k2);
        }

        //setKeyHeight();

        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(true);
        keyboardView.setOnKeyboardActionListener(listener);

        WindowUtil.hiddenSysKey(ed);
//        int inType = edit.getInputType();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            Class<EditText> cls=EditText.class;
//            try {
//                Method setShowSoftInputOnFocus=cls.getMethod("setShowSoftInputOnFocus", boolean.class);
//                setShowSoftInputOnFocus.setAccessible(false);
//                setShowSoftInputOnFocus.invoke(edit, false);
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//        }else {
//            edit.setInputType(android.text.InputType.TYPE_NULL); // disable soft input
//            edit.setInputType(inType);
//        }

    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_DONE) {// 完成
                //hideKeyboard();
                WindowUtil.hiddenKey();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {// 大小写切换
                changeKey();
                keyboardView.setKeyboard(k2);

            } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {// 数字键盘切换
                if (isnun) {
                    isnun = false;
                    keyboardView.setKeyboard(k2);
                } else {
                    isnun = true;
                    keyboardView.setKeyboard(k1);
                }
                //setKeyHeight();
            }
            else if(primaryCode == 20013){//切换中文  换成了输入“@”
//                WindowUtil.hiddenKey();
//                InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(ed,InputMethodManager.SHOW_FORCED);
                editable.insert(start, "@");
            }
            else if (primaryCode == 4896) { // 清空
                ed.setText("");
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }
    };

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        List<Keyboard.Key> keylist = k2.getKeys();
        if (isupper) {//大写切换小写
            isupper = false;
            for(Keyboard.Key key:keylist){
                if (key.label!=null && isword(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0]+32;
                }
            }
        } else {//小写切换大写
            isupper = true;
            for(Keyboard.Key key:keylist){
                if (key.label!=null && isword(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0]-32;
                }
            }
        }
    }

    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.GONE);
        }
    }

    private boolean isword(String str){
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase())>-1) {
            return true;
        }
        return false;
    }

    private void setKeyHeight(){
        if(keyboardView!=null){
            for (Keyboard.Key key : keyboardView.getKeyboard().getKeys()) {
                if(key.hashCode()==-4){
                    key.height = 100;
                }else {
                    key.height = 50;
                }
            }
        }
    }
}
