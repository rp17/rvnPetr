/**
 * @author Petr (http://www.sallyx.org/)
 */
package common.misc;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import static common.misc.WindowUtils.Window;
import static common.windows.POINT;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JPanel;

final public class CppToJava {

    final public static KeyCache keyCache = new KeyCache();
    final public static MouseCache mouseCache = new MouseCache();
    public static Window windowCache = null; // inicialized in Main.java

    public static class KeyCache {

        private Map<Integer, Boolean> keys;

        private KeyCache() {
            keys = new HashMap<Integer, Boolean>();
        }
        //copy ctor and assignment should be private

        private KeyCache(KeyCache kc) {
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException("Cloning not allowed");
        }

        public void released(KeyEvent e) {
            keys.put(e.getKeyCode(), Boolean.FALSE);
        }

        public void pressed(KeyEvent e) {
            keys.put(e.getKeyCode(), Boolean.TRUE);
        }

        public boolean keyDown(int key) {
            Boolean b = keys.get(key);
            if (b == null) {
                return false;
            }
            return b;
        }
    }

    public static class MouseCache {

        private MouseEvent moved;

        private MouseCache() {
        }
        //copy ctor and assignment should be private

        private MouseCache(MouseCache mc) {
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException("Cloning not allowed");
        }

        synchronized public void moved(MouseEvent e) {
            this.moved = e;
        }

        synchronized public void GetCursorPos(POINT MousePos) {
            if (moved == null) {
                return;
            }
            Point p = moved.getPoint();
            MousePos.setLocation(p.x, p.y);
        }
    }
    
    public static Window FindWindow() {
        return windowCache;
    }
    public static void ResizeWindow(Window hWnd, int width, int height) {
        JPanel panel = hWnd.getPanel();
        panel.setSize(width, height);
        panel.setPreferredSize(new Dimension(width, height));
        hWnd.pack();
    }


    public static boolean KEYDOWN(int c) {
        return keyCache.keyDown(c)
                || keyCache.keyDown(Character.toLowerCase(c))
                || keyCache.keyDown(Character.toUpperCase(c));
    }

    public static class IntegerRef extends AtomicReference<Integer> {

        public IntegerRef(Integer ref) {
            super(ref);
        }

        public Integer toInteger() {
            return this.get();
        }
    }

    public static class DoubleRef extends AtomicReference<Double> {

        public DoubleRef(Double ref) {
            super(ref);
        }

        /**
         * The same as get();
         *
         * @return the current value.
         */
        public Double toDouble() {
            return this.get();
        }
    }

    public static class ObjectRef<T extends Object> extends AtomicReference<T> {

        public ObjectRef(T ref) {
            super(ref);
        }

        public ObjectRef() {
            super();
        }

        public T getValue() {
            return (T) this.get();
        }
    }

    public static <T extends Object> List<T> clone(List<T> list) {
        try {
            List<T> c = list.getClass().newInstance();
            for (T t : list) {
                T copy = (T) t.getClass().getDeclaredConstructor(t.getClass()).newInstance(t);
                c.add(copy);
            }
            return c;
        } catch (Exception e) {
            throw new RuntimeException("List cloning unsupported", e);
        }
    }

    /**
     * C++ 3/2 = 2, but in Java 3/2 = 1. So I had to make this funtion to
     * simulate C++ approach.
     *
     * @param i
     * @return i/2 as in C++
     */
    public static int half(int i) {
        return (int) Math.ceil(i / 2.0);
    }
}
