package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import androidx.recyclerview.widget.ListAdapter;

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    private lateinit var crimeRecyclerView: RecyclerView
    private lateinit var noDataTextView: TextView
    private lateinit var addCrimeButton: Button
    private lateinit var lyEmptyRecyclerView:LinearLayout


    //private var adapter: CrimeAdapter? =null;
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }

    /**
     * Required interface for hosting activities
     */
    private var callbacks: Callbacks? = null;

    interface Callbacks{
        fun onCrimeSelected(crimeId:UUID);
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks=null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        //   Log.d(TAG, "Total crimes: ${crimeListViewModel.crimes.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter;

        noDataTextView = view.findViewById(R.id.empty_list_textview)
        addCrimeButton = view.findViewById(R.id.addCrimeBtn)
        lyEmptyRecyclerView = view.findViewById(R.id.ly_empty_recyclerView)
        //noDataTextView.text = getString(R.string.no_data_message)

        // updateUI()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeListViewModel.crimeListLiveData?.observe(
            viewLifecycleOwner,
            Observer { crimes ->
                crimes?.let {
                    Log.i(
                        TAG,
                        "Got crimes ${crimes.size}"
                    );
                    updateUI(crimes);
                }
            })

        addCrimeButton.setOnClickListener {
            val crime = Crime()
            crimeListViewModel.addCrime(crime)
            callbacks?.onCrimeSelected(crime.id)

        }
    }

    private fun updateUI(crimes: List<Crime>) {
        //adapter = CrimeAdapter(crimes);
      //  crimeRecyclerView.adapter = adapter;
        /* val crimes = crimeListViewModel.crimes
         adapter = CrimeAdapter(crimes)*/

        if (crimes.isNotEmpty()) {
            adapter = CrimeAdapter(crimes)
            lyEmptyRecyclerView.visibility = View.GONE
            crimeRecyclerView.adapter = adapter

            /*val adapterTemp = crimeRecyclerView.adapter as CrimeAdapter
            adapterTemp.submitList(crimes)*/
        } else {
            //the next line is belong to challenge 13 ch 14
            crimeRecyclerView.visibility = View.GONE

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
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View) {
    //   Toast.makeText(context, "${crime.title} clicked!", Toast.LENGTH_SHORT)
    //           .show()

            /*val fragment=CrimeFragment();
            val fm=activity?.supportFragmentManager
            fm?.beginTransaction()?.replace(R.id.fragment_container,fragment)?.commit()
*/
            callbacks?.onCrimeSelected(crime.id)
           /* val args = Bundle().apply {
                putSerializable("name", "Maimoona")
                putInt("age", 15)
*/
        }
    }

    /*private inner class CrimeAdapter(var crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {*/

    private inner class CrimeAdapter(var crimes: List<Crime>) :
        ListAdapter<Crime, CrimeHolder>(CrimeDiffUtil()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

        override fun getItemCount() = crimes.size
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }


    private inner class CrimeDiffUtil:
        DiffUtil.ItemCallback<Crime>(){
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem;
        }

    }
}