package com.srinjoy.libbuddy.view.fragments.admin

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.core.Constants
import com.srinjoy.libbuddy.databinding.FragmentAdminAllStudentsBinding
import com.srinjoy.libbuddy.models.Student
import com.srinjoy.libbuddy.view.activities.AdminMainActivity
import com.srinjoy.libbuddy.view.adapters.StudentsAdapter
import com.srinjoy.libbuddy.viewmodels.AdminAllStudentsViewModel
import com.srinjoy.libbuddy.viewmodels.AdminAllStudentsViewModelFactory

class AdminAllStudentsFragment : Fragment() {


    private val mViewModel: AdminAllStudentsViewModel by viewModels {
        AdminAllStudentsViewModelFactory((requireActivity().application as LibraryApplication).adminRepository)
    }

    private lateinit var mBinding: FragmentAdminAllStudentsBinding
    private lateinit var mStudentsAdapter: StudentsAdapter
    private lateinit var mCircularProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAdminAllStudentsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mStudentsAdapter = StudentsAdapter(this@AdminAllStudentsFragment)
        mBinding.rvStudents.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        mBinding.rvStudents.adapter=mStudentsAdapter
        mCircularProgressBar=mBinding.pbCircular
        mBinding.etSearchStudents.doAfterTextChanged {
            Log.i("search query",it.toString())
            mViewModel.searchDebounced(it.toString(),this@AdminAllStudentsFragment)
        }
        viewModelObservers()
    }

    private fun viewModelObservers(){
        mViewModel.loading.observe(viewLifecycleOwner){value->
            value?.let {
                if(value){
                    showLoader()
                }else{
                    stopLoader()
                }
            }
        }
        mViewModel.success.observe(viewLifecycleOwner){value->
            value?.let {
                if(value){
                    val students=mViewModel.students.value ?: listOf<Student.Student>()
                    if(students.isEmpty())
                    {
                        mBinding.tvNoStudents.visibility=View.VISIBLE
                        mBinding.rvStudents.visibility=View.GONE
                    }else{
                        mStudentsAdapter.setStudents(students)
                        mBinding.tvNoStudents.visibility=View.GONE
                        mBinding.rvStudents.visibility=View.VISIBLE
                    }

                }
            }
        }
        mViewModel.error.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (value) {
                    Toast.makeText(
                        context,
                        mViewModel.errorMessage.value.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showLoader() {
        Log.i("showing loader","true")
        mCircularProgressBar.visibility=View.VISIBLE
        mBinding.rvStudents.visibility=View.GONE
        mBinding.tvNoStudents.visibility=View.GONE
    }

    private fun stopLoader() {
        Log.i("showing loader","false")
        mCircularProgressBar.visibility=View.GONE
        mBinding.rvStudents.visibility=View.VISIBLE
        mBinding.tvNoStudents.visibility=View.GONE
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is AdminMainActivity) {
            (requireActivity() as AdminMainActivity?)!!.showBottomNavigationView()
        }
    }

    fun goToStudentProfile(id:String){
        if (requireActivity() is AdminMainActivity) {
            (requireActivity() as AdminMainActivity?)!!.hideBottomNavigationView()
        }
        val bundle = bundleOf(Constants.EXTRA_STUDENT_ID to id)
        findNavController().navigate(R.id.action_adminAllStudentsFragment_to_studentProfileFragment, bundle)
    }

}