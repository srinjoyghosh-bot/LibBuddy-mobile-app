package com.srinjoy.libbuddy.view.adapters

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.core.Utils
import com.srinjoy.libbuddy.databinding.ItemIssueHistoryBinding
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.models.Student
import com.srinjoy.libbuddy.view.fragments.admin.AdminRequestsFragment

class IssueHistoryAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<IssueHistoryAdapter.ViewHolder>() {

    private var history: MutableList<Book.IssueDetails> = mutableListOf()

    class ViewHolder(view: ItemIssueHistoryBinding) : RecyclerView.ViewHolder(view.root) {
        val tvBookName = view.tvBookName
        val tvIssueDate = view.tvIssueDate
        val ivStatus = view.ivStatus
        val tvReturnDate=view.tvReturnDate
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
        holder.tvIssueDate.text =fragment.getString(R.string.lbl_issue_date,Utils.getFormattedDate(issue.createdAt))
        if(issue.return_date!=null){
            holder.tvReturnDate.visibility= View.VISIBLE
            holder.tvReturnDate.text=fragment.getString(R.string.lbl_return_date,Utils.getFormattedDate(issue.return_date as String))
        }


        when (issue.status) {
            "pending" -> holder.ivStatus.setImageResource(R.mipmap.ic_pending)
            "approved" -> holder.ivStatus.setImageResource(R.mipmap.ic_approved)
            "rejected" -> holder.ivStatus.setImageResource(R.mipmap.ic_rejected)
            else -> holder.ivStatus.setImageResource(R.mipmap.ic_pending)
        }
        if (fragment is AdminRequestsFragment) {
            holder.itemView.setOnClickListener {
                Toast.makeText(fragment.context, "Request", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setHistory(issues: List<Book.IssueDetails>) {
        history = issues.toMutableList()
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if(position<itemCount){
            history.removeAt(position)
            notifyItemRemoved(position)
            if(fragment is AdminRequestsFragment){
                fragment.updateBorrowsListInViewModel(history)
            }
        }

    }

    fun getItemByPosition(position: Int): Book.IssueDetails? {
        if (position >= itemCount) {
            return null;
        }
        return history[position]
    }

    override fun getItemCount(): Int {
        return history.size
    }
}

class SwipeCallback(
    private val onAccept: (Int) -> Unit,
    private val onReject: (Int) -> Unit,
    private val context: Context
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val acceptIcon = ContextCompat.getDrawable(
        context, R.drawable.ic_accept
    )

    private val rejectIcon = ContextCompat.getDrawable(
        context, R.drawable.ic_reject
    )

    private val acceptBackground = ColorDrawable(Color.GREEN)
    private val rejectBackground = ColorDrawable(Color.RED)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        when (direction) {
            ItemTouchHelper.LEFT -> onReject(position)
            ItemTouchHelper.RIGHT -> onAccept(position)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val iconMargin = (itemView.height - acceptIcon!!.intrinsicHeight) / 2

        when {
            dX > 0 -> { // Swiping to the right (Accept)
                acceptBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                acceptBackground.draw(c)

                val iconTop = itemView.top + (itemView.height - acceptIcon.intrinsicHeight) / 2
                val iconLeft = itemView.left + iconMargin
                val iconRight = itemView.left + iconMargin + acceptIcon.intrinsicWidth
                val iconBottom = iconTop + acceptIcon.intrinsicHeight

                acceptIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                acceptIcon.draw(c)
            }
            dX < 0 -> { // Swiping to the left (Reject)
                rejectBackground.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                rejectBackground.draw(c)

                val iconTop = itemView.top + (itemView.height - rejectIcon!!.intrinsicHeight) / 2
                val iconLeft = itemView.right - iconMargin - rejectIcon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                val iconBottom = iconTop + rejectIcon.intrinsicHeight

                rejectIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                rejectIcon.draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
