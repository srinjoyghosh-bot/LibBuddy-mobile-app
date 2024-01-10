package com.srinjoy.libbuddy.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.srinjoy.libbuddy.databinding.ItemBookListBinding
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.view.fragments.admin.AdminBooksFragment
import com.srinjoy.libbuddy.view.fragments.student.StudentBooksFragment

class AllBooksAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<AllBooksAdapter.ViewHolder>() {

    private var books: List<Book.Book> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemBookListBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = books[position]
        holder.tvBookID.text = book.id
        holder.tvBookName.text = book.name
        holder.itemView.setOnClickListener {
            when (fragment) {
                is StudentBooksFragment -> fragment.goToBookDetails(book)
                is AdminBooksFragment -> fragment.goToBookDetails(book)
            }

        }

    }

    override fun getItemCount(): Int {
        return books.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBooks(list: List<Book.Book>) {
        books = list
        notifyDataSetChanged()
    }

    class ViewHolder(view: ItemBookListBinding) : RecyclerView.ViewHolder(view.root) {
        val tvBookID = view.tvBookId
        val tvBookName = view.tvBookName
        val ivBookImage = view.ivBookImage
    }
}