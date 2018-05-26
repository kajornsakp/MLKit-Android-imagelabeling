package com.example.kajornsakp.firebaseplayground.ui.result

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.net.toUri
import com.example.kajornsakp.firebaseplayground.R
import kotlinx.android.synthetic.main.fragment_result.*

class ResultFragment : Fragment() {

    companion object {
        fun newInstance() = ResultFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            val args = ResultFragmentArgs.fromBundle(it)
            imageView.setImageURI(args.uri.toUri())
            textView.text = args.output
        }
    }

}
