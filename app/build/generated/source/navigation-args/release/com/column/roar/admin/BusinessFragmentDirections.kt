package com.column.roar.admin

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.column.roar.R

public class BusinessFragmentDirections private constructor() {
  public companion object {
    public fun actionBusinessFragmentToAdminManageMarketers(): NavDirections =
        ActionOnlyNavDirections(R.id.action_businessFragment_to_adminManageMarketers)
  }
}
