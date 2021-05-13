package ca.uwaterloo.cs349;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class EditFragment extends Fragment {

    private SharedViewModel mViewModel;
    private Button completeButton;
    private List<OneStroke> strokes;
//    private OneStroke currStroke;
    int position;
    AdditionGestureRecognitionView additionGestureRecognitionView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_edit, container, false);
        final TextView textView = root.findViewById(R.id.text_edit);
//        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s + " - Edit");
//            }
//        });

        additionGestureRecognitionView = new AdditionGestureRecognitionView(this.getContext());
        additionGestureRecognitionView.curStroke = (OneStroke)this.strokes.get(position).clone();

        ViewGroup view_group = (ViewGroup) root.findViewById(R.id.edit_layout);
        view_group.addView(additionGestureRecognitionView);

        completeButton = root.findViewById(R.id.edit_complete_button);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                additionGestureRecognitionView.curStroke.save();
                additionGestureRecognitionView.curStroke.strokeName = strokes.get(position).strokeName;
                strokes.set(position,(OneStroke)additionGestureRecognitionView.curStroke.clone());
                Fragment lib = new LibraryFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, lib );
                transaction.addToBackStack(null); 
                transaction.commit();
            }
        });

        return root;
    }

    EditFragment(int position){
        strokes = SharedViewModel.savedStrokes;
        this.position = position;
//        this.currStroke = strokes.get(position);
    }


}
