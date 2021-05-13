package ca.uwaterloo.cs349;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class AdditionFragment extends Fragment {

    private SharedViewModel mViewModel;

    Button saveButton;
    Button clearButton;
    AdditionGestureRecognitionView additionGestureRecognitionView;

    static int click = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addition, container, false);
        final TextView textView = root.findViewById(R.id.text_addition);
//        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
////                textView.setText(s + " - Addition");
//            }
//        });

        ViewGroup view_group = (ViewGroup) root.findViewById(R.id.addition_layout);
        additionGestureRecognitionView = new AdditionGestureRecognitionView(this.getContext());
        view_group.addView(additionGestureRecognitionView);

        saveButton = root.findViewById(R.id.save_buttom);
        clearButton = root.findViewById(R.id.clear_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (additionGestureRecognitionView.curStroke == null) return;

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
                alertDialog.setTitle("Input your gesture name");
                final EditText textInput = new EditText(requireContext());
                textInput.setInputType(InputType.TYPE_CLASS_TEXT);
                alertDialog.setView(textInput);

                alertDialog.setPositiveButton("save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            additionGestureRecognitionView.curStroke.strokeName = textInput.getText().toString();
                        }catch (Exception e){
                            additionGestureRecognitionView.curStroke.strokeName = "";
                        }
                        additionGestureRecognitionView.curStroke.save();
                        SharedViewModel.savedStrokes.add((OneStroke)additionGestureRecognitionView.curStroke.clone());
                        additionGestureRecognitionView.curStroke = null;
                        additionGestureRecognitionView.postInvalidate();

//                        byte[] bytes = OneStroke.convertCurrStateToByte();
//                        string = Base64.getEncoder().encodeToString(bytes);


                    }
                });

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (click % 2 == 0) AdditionGestureRecognitionView.currStroke.setCentroid();
//                if (click % 3 == 0) AdditionGestureRecognitionView.currStroke.resample(128);
//                if (click % 5 == 0) AdditionGestureRecognitionView.currStroke.rotate();
//                additionGestureRecognitionView.postInvalidate();
                additionGestureRecognitionView.curStroke = null;
                additionGestureRecognitionView.postInvalidate();
//
//                try{
//                    OneStroke.loadCurrStatefromByte(Base64.getDecoder().decode(string));
//                }catch (Exception e){
//                    e.printStackTrace();
//                }



            }
        });

        return root;
    }

    static String string;


}