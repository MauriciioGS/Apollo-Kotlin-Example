package mx.mauriciogs.consuminggraphql

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import apolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.CharactersListQuery
import mx.mauriciogs.consuminggraphql.databinding.FragmentCharactersListBinding

class CharactersListFragment : Fragment() {

    private lateinit var binding: FragmentCharactersListBinding
    private var characters = emptyList<CharactersListQuery.Result>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharactersListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
    }

    private fun getData() {
        lifecycleScope.launchWhenResumed {
            val response = try {
                apolloClient.query(CharactersListQuery()).execute()
            } catch (exception: ApolloException){
                Toast.makeText(requireActivity(), "Failure: ${exception.message}", Toast.LENGTH_LONG).show()
                return@launchWhenResumed
            }
            characters = response.data?.characters?.results?.filterNotNull() ?: emptyList()
            if (characters.isNotEmpty() && !response.hasErrors()){
                setRecyclerView()
            }
        }
    }

    private fun setRecyclerView() {
        val adapter = CharacterListAdapter(characters)
        binding.rvCharacters.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvCharacters.adapter = adapter


    }

}