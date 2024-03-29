package com.bakjoul.go4lunch.ui.restaurants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bakjoul.go4lunch.R;
import com.bakjoul.go4lunch.databinding.FragmentRestaurantsBinding;
import com.bakjoul.go4lunch.ui.details.DetailsActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RestaurantsFragment extends Fragment implements RestaurantsAdapter.OnRestaurantClickListener {

    private FragmentRestaurantsBinding binding;

    @NonNull
    public static RestaurantsFragment newInstance() {
        return new RestaurantsFragment();
    }

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

        RecyclerView recyclerView = binding.restaurantsRecyclerView;
        RestaurantsAdapter adapter = new RestaurantsAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(requireContext(), R.drawable.custom_divider)));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(itemDecoration);

        viewModel.getRestaurantsViewStateLiveData().observe(getViewLifecycleOwner(), viewState -> {
                recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        linearLayoutManager.scrollToPosition(0);
                        recyclerView.removeOnLayoutChangeListener(this);
                    }
                });

                if (viewState.isProgressBarVisible()) {
                    binding.listProgressBar.setVisibility(View.VISIBLE);
                } else {
                    binding.listProgressBar.setVisibility(View.GONE);
                }

                adapter.submitList(viewState.getRestaurantsItemViewStates());

                if (viewState.isEmptyStateVisible()) {
                    binding.restaurantsEmpty.setVisibility(View.VISIBLE);
                } else {
                    binding.restaurantsEmpty.setVisibility(View.GONE);
                }
            }
        );

        viewModel.getIsRetryBarVisibleSingleLiveEvent().observe(getViewLifecycleOwner(), isRetryBarVisible -> {
            if (isRetryBarVisible) {
                Snackbar
                    .make(binding.getRoot(), R.string.snackbar_message, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.snackbar_retry, v -> viewModel.onRetryButtonClicked())
                    .show();
            }
        });

        viewModel.getIsUserSearchUnmatchedSingleLiveEvent().observe(getViewLifecycleOwner(), isSearchUnmatched -> {
                if (isSearchUnmatched) {
                    Toast.makeText(requireContext(), R.string.no_match, Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    @Override
    public void onRestaurantClicked(int position) {
        SearchView searchView = requireActivity().findViewById(R.id.main_SearchView);
        // Closes search view if it has focus and input is empty
        if (searchView.hasFocus() && searchView.getQuery().length() == 0) {
            searchView.setIconified(true);
            searchView.onActionViewCollapsed();
            return;
        }

        if (binding.restaurantsRecyclerView.getLayoutManager() != null) {
            startActivity(
                DetailsActivity.navigate(
                    requireContext(),
                    binding.restaurantsRecyclerView.getLayoutManager().findViewByPosition(position).getTag().toString()
                ));
        }
    }
}
