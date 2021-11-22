package com.column.roar.dashBoard.lodge

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.column.roar.R

public class CareerFragmentDirections private constructor() {
  public companion object {
    public fun actionCareerFragmentToLodgeDetailUpload(): NavDirections =
        ActionOnlyNavDirections(R.id.action_careerFragment_to_lodgeDetailUpload)

    public fun actionCareerFragmentToEditLodgePager(): NavDirections =
        ActionOnlyNavDirections(R.id.action_careerFragment_to_editLodgePager)
  }
}
