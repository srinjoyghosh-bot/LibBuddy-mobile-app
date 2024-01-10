package com.srinjoy.libbuddy.view.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.core.Utils
import com.srinjoy.libbuddy.databinding.ItemIssueHistoryBinding
import com.srinjoy.libbuddy.models.Student

class IssueHistoryAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<IssueHistoryAdapter.ViewHolder>() {

    private var history: List<Student.IssueDetails> = listOf()

    class ViewHolder(view: ItemIssueHistoryBinding) : RecyclerView.ViewHolder(view.root) {
        val tvBookName = view.tvBookName
        val tvIssueDate = view.tvIssueDate
        val ivStatus = view.ivStatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemIssueHistoryBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val issue = history[position]
        holder.tvBookName.text = issue.book_id
        holder.tvIssueDate.text = Utils.getFormattedDate(issue.createdAt)
        when (issue.status) {
            "pending" -> holder.ivStatus.setImageResource(R.mipmap.ic_pending)
            "approved" -> holder.ivStatus.setImageResource(R.mipmap.ic_approved)
            "rejected" -> holder.ivStatus.setImageResource(R.mipmap.ic_rejected)
            else -> holder.ivStatus.setImageResource(R.mipmap.ic_pending)
        }
    }

    fun setHistory(issues: List<Student.IssueDetails>) {
        history = issues
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return history.size
    }
}