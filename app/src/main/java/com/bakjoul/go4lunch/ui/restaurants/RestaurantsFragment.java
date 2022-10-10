package com.bakjoul.go4lunch.ui.restaurants;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.data.model.ErrorType;
import com.bakjoul.go4lunch.databinding.FragmentRestaurantsBinding;
import com.bakjoul.go4lunch.ui.details.DetailsActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RestaurantsFragment extends Fragment implements RestaurantsAdapter.OnRestaurantClickListener {

   public static RestaurantsFragment newInstance() {
      return new RestaurantsFragment();
   }

   private FragmentRestaurantsBinding binding;

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      binding = FragmentRestaurantsBinding.inflate(inflater, container, false);
      return binding.getRoot();
   }

   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);

      RestaurantsViewModel viewModel = new ViewModelProvider(this).get(RestaurantsViewModel.class);

      RestaurantsAdapter adapter = new RestaurantsAdapter(this);
      binding.restaurantsRecyclerView.setAdapter(adapter);
      DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
      itemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(requireContext(), R.drawable.custom_divider)));
      binding.restaurantsRecyclerView.addItemDecoration(itemDecoration);

      viewModel.getRestaurantsViewState().observe(getViewLifecycleOwner(), viewState -> {
             if (!viewState.isProgressBarVisible()) {
                binding.listProgressBar.setVisibility(View.GONE);
             } else {
                binding.listProgressBar.setVisibility(View.VISIBLE);
             }
             adapter.submitList(viewState.getRestaurantsItemViewStates());
          }
      );

      viewModel.getErrorTypeSingleLiveEvent().observe(getViewLifecycleOwner(), errorType -> {
         if (errorType == ErrorType.TIMEOUT) {
            Log.d("test", "onViewCreated: snackbar");
            Snackbar
                .make(binding.getRoot(), R.string.snackbar_timeout, Snackbar.LENGTH_INDEFINITE)
                .setAnchorView(binding.listProgressBar)
                .setAction(R.string.snackbar_retry, v -> {
                   // Do something
                })
                .show();
         }
      });
   }

   @Override
   public void OnRestaurantClicked(int position) {
      DetailsActivity.navigate(
          binding.restaurantsRecyclerView.getLayoutManager().findViewByPosition(position).getTag().toString(), getActivity()
      );
   }
}
