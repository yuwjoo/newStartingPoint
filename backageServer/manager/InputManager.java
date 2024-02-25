package backageServer.manager;

import android.annotation.SuppressLint;
import android.view.InputEvent;
import android.view.MotionEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressLint("PrivateApi,DiscouragedPrivateApi")
public final class InputManager {

    public static final int INJECT_INPUT_EVENT_MODE_ASYNC = 0; // 异步处理事件
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT = 1;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = 2;

    private static InputManager inputManager; // InputManager实例
    private final Object manager; // 内部InputManager实例
    private Method injectInputEventMethod; // 内部注入事件方法

    private static Method setDisplayIdMethod;
    private static Method setActionButtonMethod;

    public InputManager(Object manager) {
        this.manager = manager;
    }

    /**
     * 获取InputManager类
     *
     * @return InputManager类
     */
    private static Class<?> getInputManagerClass() {
        try {
            // Parts of the InputManager class have been moved to a new InputManagerGlobal
            // class in Android 14 preview
            return Class.forName("android.hardware.input.InputManagerGlobal");
        } catch (ClassNotFoundException e) {
            return android.hardware.input.InputManager.class;
        }
    }

    /**
     * 获取InputManager实例
     *
     * @return InputManager实例
     */
    public static InputManager getInstance() {
        if (inputManager == null) {
            try {
                Class<?> inputManagerClass = getInputManagerClass();
                Method getInstanceMethod = inputManagerClass.getDeclaredMethod("getInstance");
                Object im = getInstanceMethod.invoke(null);
                inputManager = new InputManager(im);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new AssertionError(e);
            }
        }
        return inputManager;
    }

    /**
     * 获取内部注入事件方法
     *
     * @return 内部注入事件方法
     */
    private Method getInjectInputEventMethod() throws NoSuchMethodException {
        if (injectInputEventMethod == null) {
            injectInputEventMethod = manager.getClass().getMethod("injectInputEvent", InputEvent.class, int.class);
        }
        return injectInputEventMethod;
    }

    /**
     * 注入输入事件
     *
     * @param inputEvent 输入事件对象
     * @param mode       模式
     * @return 是否成功
     */
    public boolean injectInputEvent(InputEvent inputEvent, int mode) {
        try {
            Method method = getInjectInputEventMethod();
            return (boolean) method.invoke(manager, inputEvent, mode);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            return false;
        }
    }

    private static Method getSetDisplayIdMethod() throws NoSuchMethodException {
        if (setDisplayIdMethod == null) {
            setDisplayIdMethod = InputEvent.class.getMethod("setDisplayId", int.class);
        }
        return setDisplayIdMethod;
    }

    public static boolean setDisplayId(InputEvent inputEvent, int displayId) {
        try {
            Method method = getSetDisplayIdMethod();
            method.invoke(inputEvent, displayId);
            return true;
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            return false;
        }
    }

    private static Method getSetActionButtonMethod() throws NoSuchMethodException {
        if (setActionButtonMethod == null) {
            setActionButtonMethod = MotionEvent.class.getMethod("setActionButton", int.class);
        }
        return setActionButtonMethod;
    }

    public static boolean setActionButton(MotionEvent motionEvent, int actionButton) {
        try {
            Method method = getSetActionButtonMethod();
            method.invoke(motionEvent, actionButton);
            return true;
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            return false;
        }
    }
}
