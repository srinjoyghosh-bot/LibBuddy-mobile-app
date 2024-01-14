package com.srinjoy.libbuddy.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.srinjoy.libbuddy.databinding.ItemStudentListBinding
import com.srinjoy.libbuddy.models.Student
import com.srinjoy.libbuddy.view.fragments.admin.AdminAllStudentsFragment

class StudentsAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<StudentsAdapter.ViewHolder>() {

    private var students: List<Student.Student> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemStudentListBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.tvStudentName.text = student.name
        holder.tvStudentId.text = student.id ?: student.enrollment_id
        holder.itemView.setOnClickListener {
            if (fragment is AdminAllStudentsFragment) {
                (student.id
                    ?: student.enrollment_id)?.let { it1 -> fragment.goToStudentProfile(it1) }
            }
        }
    }

    override fun getItemCount(): Int {
        return students.size
    }

    fun setStudents(studentList: List<Student.Student>) {
        students = studentList
        notifyDataSetChanged()
    }

    class ViewHolder(view: ItemStudentListBinding) : RecyclerView.ViewHolder(view.root) {
        val tvStudentName = view.tvStudentName
        val tvStudentId = view.tvStudentId
    }
}