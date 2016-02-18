package at.domkog.dwp.ui.options;

import java.util.HashMap;

/**
 * Created by Dominik on 24.01.2016.
 */
public class Options {

    public HashMap<String, OptionValue> options;

    public Options() {
        options = new HashMap<>();
    }

    public void add(String key, OptionValue value) {
        options.put(key, value);
    }

    public OptionValue get(String key) {
        return options.get(key);
    }

    public void set(String key, Object value) {
        this.options.get(key).setValue(value);
    }

    public static class OptionValue {

        private OptionValueChangeListener listener;
        private Object value;

        public OptionValue(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) {
            if(listener != null) listener.onChange(this, this.value, value);
            this.value = value;
        }

        public boolean asBoolean() {
            return (boolean) this.value;
        }

        public String asString() {
            return (String) this.value;
        }

        public byte asByte() {
            return Byte.parseByte(this.asString());
        }

        public short asShort() {
            return Short.parseShort(this.asString());
        }

        public int asInteger() {
            return Integer.parseInt(this.asString());
        }

        public long asLong() {
            return Long.parseLong(this.asString());
        }

        public double asDouble() {
            return Double.parseDouble(this.asString());
        }

    }

    public static interface OptionValueChangeListener {

        public void onChange(OptionValue value, Object from ,Object to);

    }

}
