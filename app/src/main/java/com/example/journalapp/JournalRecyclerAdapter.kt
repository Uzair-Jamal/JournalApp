package com.example.journalapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.journalapp.databinding.JournalRowBinding

class JournalRecyclerAdapter(
    val context: Context, private var journalList:List<Journal>
    ): RecyclerView.Adapter<JournalRecyclerAdapter.MyViewHolder>()
{
    private lateinit var binding : JournalRowBinding

    // ViewHolder
    class MyViewHolder(private var binding: JournalRowBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(journal:Journal){
            binding.journal = journal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = JournalRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = journalList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val journal = journalList[position]
        holder.bind(journal)
    }
}