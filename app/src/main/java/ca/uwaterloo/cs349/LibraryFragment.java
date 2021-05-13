package ca.uwaterloo.cs349;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LibraryFragment extends Fragment implements StrokeAdapter.OnItemListener {

    private SharedViewModel mViewModel;
    RecyclerView strokesRecycler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_library, container, false);
        final TextView textView = root.findViewById(R.id.text_library);
        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s + " - Library");
            }
        });

        strokesRecycler = (RecyclerView)root.findViewById(R.id.stroke_recycler);

        StrokeAdapter adapter = new StrokeAdapter(this);
        strokesRecycler.setAdapter(adapter);
        strokesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));





        return root;
    }

    @Override
    public void onItemClick(int position) {
        Fragment bf = new EditFragment(position);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, bf );
        transaction.addToBackStack(null);
        transaction.commit();
    }
}