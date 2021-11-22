package com.column.roar.home

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.column.roar.R

public class FilterFragmentDirections private constructor() {
  public companion object {
    public fun actionFilterFragmentToLodgeDetail(): NavDirections =
        ActionOnlyNavDirections(R.id.action_filterFragment_to_lodgeDetail)
  }
}
