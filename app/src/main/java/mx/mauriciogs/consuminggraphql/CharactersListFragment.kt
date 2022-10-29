package mx.mauriciogs.consuminggraphql

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import apolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.example.CharactersListQuery
import kotlinx.coroutines.channels.Channel
import mx.mauriciogs.consuminggraphql.databinding.FragmentCharactersListBinding

class CharactersListFragment : Fragment() {

    private lateinit var binding: FragmentCharactersListBinding
    private var characters = mutableListOf<CharactersListQuery.Result>()

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
        val adapter = CharacterListAdapter(characters)
        binding.rvCharacters.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvCharacters.adapter = adapter

        val channel = Channel<Unit>(Channel.CONFLATED)
        channel.trySend(Unit)

        adapter.onEndOfListReached = {
            channel.trySend(Unit)
        }

        lifecycleScope.launchWhenResumed {
            var nextPage: Int? = 1
            for(item in channel){
                val response = try {
                    apolloClient.query(CharactersListQuery(Optional.present(nextPage))).execute()
                } catch (exception: ApolloException){
                    Toast.makeText(requireActivity(), "Failure: ${exception.message}", Toast.LENGTH_LONG).show()
                    return@launchWhenResumed
                }
                val newCharacters = response.data?.characters?.results?.filterNotNull() as MutableList<CharactersListQuery.Result>
                if (newCharacters.isNotEmpty() && !response.hasErrors()){
                    characters.addAll(newCharacters)
                    adapter.notifyItemRangeChanged(characters.indexOf(characters.first()), characters.size)
                }

                nextPage = response.data?.characters?.info?.next
                if (nextPage == null){
                    Toast.makeText(requireActivity(),"next nulo",Toast.LENGTH_LONG).show()
                    break
                }
            }

            adapter.onEndOfListReached = null
            channel.close()
        }
    }

}
