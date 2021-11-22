package com.column.roar.databinding;
import com.column.roar.R;
import com.column.roar.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class FragmentVerifyNumberBindingImpl extends FragmentVerifyNumberBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.backBtn, 1);
        sViewsWithIds.put(R.id.guideline1, 2);
        sViewsWithIds.put(R.id.guideline2, 3);
        sViewsWithIds.put(R.id.text, 4);
        sViewsWithIds.put(R.id.textView5, 5);
        sViewsWithIds.put(R.id.view6, 6);
        sViewsWithIds.put(R.id.textInputLayout, 7);
        sViewsWithIds.put(R.id.phoneNumber, 8);
        sViewsWithIds.put(R.id.nextBtn, 9);
        sViewsWithIds.put(R.id.progressBar, 10);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public FragmentVerifyNumberBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 11, sIncludes, sViewsWithIds));
    }
    private FragmentVerifyNumberBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.ImageView) bindings[1]
            , (androidx.constraintlayout.widget.Guideline) bindings[2]
            , (androidx.constraintlayout.widget.Guideline) bindings[3]
            , (com.google.android.material.button.MaterialButton) bindings[9]
            , (com.google.android.material.textfield.TextInputEditText) bindings[8]
            , (android.widget.ProgressBar) bindings[10]
            , (android.widget.TextView) bindings[4]
            , (com.google.android.material.textfield.TextInputLayout) bindings[7]
            , (android.widget.TextView) bindings[5]
            , (android.view.View) bindings[6]
            );
        this.mboundView0 = (androidx.constraintlayout.widget.ConstraintLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}