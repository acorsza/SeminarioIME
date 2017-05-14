package br.usp.ime.mac5743.ep1.seminarioime.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.usp.ime.mac5743.ep1.seminarioime.R;
import br.usp.ime.mac5743.ep1.seminarioime.pojo.Student;

/**
 * Created by aderleifilho on 11/05/17.
 */

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentViewHolder> {

    private final String CLASS_NAME = "StudentListAdapter";

    private List<Student> mStudents;
    private Context mContext;

    public class StudentViewHolder extends RecyclerView.ViewHolder {

        TextView tvNusp;
        TextView tvName;

        StudentViewHolder(View itemView, Context pContext, final List<Student> pStudents) {
            super(itemView);
            mContext = pContext;
            mStudents = pStudents;
            tvNusp = (TextView) itemView.findViewById(R.id.student_list_item_nusp);
            tvName = (TextView) itemView.findViewById(R.id.student_list_item_name);
        }
    }

    List<Student> listStudents;
    Context context;

    public StudentListAdapter(List<Student> pStudents, Context pContext) {
        this.listStudents = pStudents;
        this.context = pContext;
    }

    public void setStudents(List<Student> pStudents) {
        this.listStudents = pStudents;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_student, viewGroup, false);
        StudentViewHolder studentViewHolder = new StudentViewHolder(view, context, listStudents);
        return studentViewHolder;
    }

    @Override
    public void onBindViewHolder(StudentViewHolder studentViewHolder, int i) {
        studentViewHolder.tvNusp.setText(listStudents.get(i).getNusp());
        studentViewHolder.tvName.setText(listStudents.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return listStudents.size();
    }
}
