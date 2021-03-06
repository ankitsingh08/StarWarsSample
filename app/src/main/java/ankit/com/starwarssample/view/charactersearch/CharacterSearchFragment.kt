package ankit.com.starwarssample.view.charactersearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ankit.com.starwarssample.R
import ankit.com.starwarssample.databinding.CharacterSearchFragmentBinding
import ankit.com.starwarssample.model.CharacterPresentationModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by AnkitSingh on 12/12/20.
 */
@AndroidEntryPoint
class CharacterSearchFragment : Fragment(), StarWarCharactersAdapter.OnClickHandler {

    private val characterSearchViewModel: CharacterSearchViewModel by viewModels()

    private lateinit var binding: CharacterSearchFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CharacterSearchFragmentBinding.inflate(inflater, container, false)
        initializeUI()
        return binding.root
    }

    private fun initializeUI() {
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.app_name)
        binding.viewModel = characterSearchViewModel

        val adapter =
            StarWarCharactersAdapter(
                this
            )

        binding.rvCharacters.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        binding.rvCharacters.adapter = adapter

        binding.search.clearFocus()

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotBlank()) {
                    characterSearchViewModel.searchStarWarCharacters(newText)
                    binding.loader.visibility = View.VISIBLE
                }
                return false
            }
        })

        characterSearchViewModel.characters.observe(viewLifecycleOwner, Observer {
            binding.loader.visibility = View.GONE
            adapter.submitList(it)
        })

    }

    override fun onItemClick(character: CharacterPresentationModel) {
          val bundle = Bundle()
          bundle.putParcelable("character", character)
          findNavController().navigate(R.id.characterDetailsFragment, bundle)
    }
}