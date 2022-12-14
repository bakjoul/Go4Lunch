package com.bakjoul.go4lunch.ui.workmates;

import android.os.Bundle;
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
import com.bakjoul.go4lunch.databinding.FragmentWorkmatesBinding;
import com.bakjoul.go4lunch.ui.details.DetailsActivity;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkmatesFragment extends Fragment implements WorkmatesAdapter.OnWorkmateClickListener {

    private FragmentWorkmatesBinding binding;

    @NonNull
    public static WorkmatesFragment newInstance() {
        return new WorkmatesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WorkmatesViewModel viewModel = new ViewModelProvider(this).get(WorkmatesViewModel.class);

        WorkmatesAdapter adapter = new WorkmatesAdapter(this);
        binding.workmatesRecyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(requireContext(), R.drawable.custom_divider)));
        binding.workmatesRecyclerView.addItemDecoration(itemDecoration);

        viewModel.getWorkmatesViewStateMediatorLiveData().observe(getViewLifecycleOwner(), workmatesViewState -> {
                adapter.submitList(workmatesViewState.getWorkmatesItemViewStateList());

                if (workmatesViewState.isEmptyStateVisible()) {
                    binding.workmatesEmpty.setVisibility(View.VISIBLE);
                } else {
                    binding.workmatesEmpty.setVisibility(View.GONE);
                }
            }
        );
    }

    @Override
    public void onWorkmateClicked(int position) {
        if (binding.workmatesRecyclerView.getLayoutManager() != null
            && binding.workmatesRecyclerView.getLayoutManager().findViewByPosition(position).getTag() != null) {
            DetailsActivity.navigate(
                binding.workmatesRecyclerView.getLayoutManager().findViewByPosition(position).getTag().toString(), getActivity()
            );
        }
    }
}
