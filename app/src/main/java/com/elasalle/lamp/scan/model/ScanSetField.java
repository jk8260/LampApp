package com.elasalle.lamp.scan.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.elasalle.lamp.R;
import com.elasalle.lamp.model.user.Field;
import com.elasalle.lamp.util.ResourcesUtil;

public class ScanSetField  implements Parcelable {

    private String id;
    private Field field;
    private String scanFieldLabel;
    private String scanFieldValue;
    private boolean isCarryForward = false;
    private boolean isShowField = true;
    private int order;
    private boolean isSelected = false;

    public ScanSetField(Field field) {
        this.field = field;
        this.scanFieldLabel = field.getName();
        this.id = field.getFieldId();
    }

    protected ScanSetField(Parcel in) {
        scanFieldLabel = in.readString();
        scanFieldValue = in.readString();
        isCarryForward = in.readByte() != 0;
        isShowField = in.readByte() != 0;
        order = in.readInt();
        isSelected = in.readByte() != 0;
        field = in.readParcelable(Field.class.getClassLoader());
        id = field.getFieldId();
    }

    public String getId() {
        return id;
    }

    public String getScanFieldLabel() {
        return scanFieldLabel;
    }

    public void setScanFieldLabel(String scanFieldLabel) {
        this.scanFieldLabel = scanFieldLabel;
    }

    public String getScanFieldValue() {
        final int isShowStringId = isShowField ? R.string.field_show : R.string.field_hide;
        final int isCarryForwardStringId = isCarryForward ? R.string.field_carry_forward : R.string.field_always_ask;
        scanFieldValue = ResourcesUtil.getString(isShowStringId) + ", " + ResourcesUtil.getString(isCarryForwardStringId);
        return scanFieldValue;
    }

    public void setScanFieldValue(String scanFieldValue) {
        this.scanFieldValue = scanFieldValue;
    }

    public boolean isCarryForward() {
        return isCarryForward;
    }

    public void setCarryForward(boolean carryForward) {
        isCarryForward = carryForward;
    }

    public boolean isShowField() {
        return isShowField;
    }

    public void setShowField(boolean showField) {
        isShowField = showField;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public boolean isRequired() {
        return this.field.isRequired();
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ScanSetField) {
            final ScanSetField scanSetField = (ScanSetField) object;
            return id.equals(scanSetField.getId());
        } else {
            throw new ClassCastException("Object must be instance of ScanSetField to compare with equals.");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(scanFieldLabel);
        dest.writeString(scanFieldValue);
        dest.writeByte((byte) (isCarryForward ? 1 : 0));
        dest.writeByte((byte) (isShowField ? 1 : 0));
        dest.writeInt(order);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeParcelable(field, 0);
    }

    public static final Creator<ScanSetField> CREATOR = new Creator<ScanSetField>() {
        @Override
        public ScanSetField createFromParcel(Parcel in) {
            return new ScanSetField(in);
        }

        @Override
        public ScanSetField[] newArray(int size) {
            return new ScanSetField[size];
        }
    };
}
