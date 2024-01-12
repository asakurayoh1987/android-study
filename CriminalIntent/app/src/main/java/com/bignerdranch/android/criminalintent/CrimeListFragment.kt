package com.bignerdranch.android.criminalintent

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {
    /**
     * Required interface for hosting activites
     */
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null

    private lateinit var crimeRecylerView: RecyclerView
    private lateinit var crimeEmptyPlaceHolder: LinearLayout
    private lateinit var addCrimeButton: Button
    private var adapter: CrimeAdapter? = CrimeAdapter()
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this)[CrimeListViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 告之FragmentManager，CrimeListFragment需要接收选项菜单函数回调
        // Fragment.onCreateOptionsMenu(Menu, MenuInflater)函数是由FragmentManager负责调用的。
        // 因此，当activity接收到操作系统的onCreateOptionsMenu(...)函数回调请求时，我们必须明确告诉FragmentManager，
        // 其管理的fragment应接收onCreateOptionsMenu(...)函数的调用指令
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeEmptyPlaceHolder = view.findViewById(R.id.crime_empty_placeholder)
        addCrimeButton = view.findViewById(R.id.new_crime)

        addCrimeButton.setOnClickListener {
            val crime = Crime()
            crimeListViewModel.addCrime(crime)
            callbacks?.onCrimeSelected(crime.id)
        }

        crimeRecylerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecylerView.layoutManager = LinearLayoutManager(context)
        crimeRecylerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner
        ) { crimes ->
            crimes?.let {
                Log.i(TAG, "Got crimes ${crimes.size}")
                updateUI(crimes)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // 将布局中定义的菜单项目填充到Menu实例中
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                // 返回true值以表明任务已完成。如果返回false值，
                // 就调用托管activity的onOptionsItemSelected(MenuItem)函数继续
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(crimes: List<Crime>) {
        if (crimes.isNotEmpty()) {
            crimeRecylerView.visibility = View.VISIBLE
            crimeEmptyPlaceHolder.visibility = View.GONE
            adapter?.submitList(crimes)
        } else {
            crimeRecylerView.visibility = View.GONE
            crimeEmptyPlaceHolder.visibility = View.VISIBLE
        }
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = DateFormat.format("EEE, MMM dd, yyyy", crime.date)
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View?) {
            Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
            callbacks?.onCrimeSelected(crime.id)
        }

    }

    private inner class CrimeDiffCallback : DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            val result = oldItem.id == newItem.id
            if (!result) {
                Log.i(TAG, "check and find different id")
            }
            return result
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            val result = oldItem.title == newItem.title &&
                    oldItem.date == newItem.date &&
                    oldItem.isSolved == newItem.isSolved

            if (!result) {
                Log.i(TAG, "check and find different content")
            }

            return result
        }

    }

    private inner class CrimeAdapter :
        ListAdapter<Crime, CrimeHolder>(CrimeDiffCallback()) {

        // Adapter.onCreateViewHolder(...)负责创建要显示的视图，将其封装到一个ViewHolder里并返回结果
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view =
                layoutInflater.inflate(
                    R.layout.list_item_crime,
                    parent,
                    false
                )
            return CrimeHolder(view)
        }

        // Adapter.onBindViewHolder(holder: CrimeHolder, position: Int)负责将数据集里指定位置的crime数据发送给指定ViewHolder
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = getItem(position)
            holder.bind(crime)
        }
    }
}