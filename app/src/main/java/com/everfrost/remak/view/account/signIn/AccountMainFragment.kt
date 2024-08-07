package com.everfrost.remak.view.account.signIn

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.everfrost.remak.R
import com.everfrost.remak.UtilitySystem
import com.everfrost.remak.adapter.SmallIconAdapter
import com.everfrost.remak.adapter.SmallIconItemOffsetDecoration
import com.everfrost.remak.adapter.TestItemOffsetDecoration
import com.everfrost.remak.adapter.TestRVAdapter
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.AccountMainFragmentBinding
import java.util.Timer
import java.util.TimerTask

class AccountMainFragment : Fragment() {
    private lateinit var binding: AccountMainFragmentBinding
    private var bigIconTimer = Timer()
    private var smallIconTimer = Timer()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: TestRVAdapter
    private lateinit var smallIconLayoutManager: LinearLayoutManager
    private lateinit var smallIconAdapter: SmallIconAdapter

    lateinit var signInRepository: TokenRepository
    private val iconData = listOf(
        "통신문.jpg", "과제.hwp", "잼짤.png", "문서.docx", "통신문.jpg", "과제.hwp", "잼짤.png", "문서.docx",
        "통신문.jpg", "과제.hwp", "잼짤.png", "문서.docx",
        "통신문.jpg", "과제.hwp", "잼짤.png", "문서.docx",
        "통신문.jpg", "과제.hwp", "잼짤.png", "문서.docx"
    )

    private val smallIconData = listOf(
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
        listOf("기사", "오늘의 주식 뉴스"),
        listOf("메모", "오늘 해야할 일"),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AccountMainFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }




        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TestRVAdapter(iconData)
        binding.bigIconRecyclerView.adapter = adapter
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.bigIconRecyclerView.layoutManager = layoutManager
        binding.bigIconRecyclerView.setOnTouchListener { _, _ ->
            true
        }
        val itemDecoration = TestItemOffsetDecoration(30)
        binding.bigIconRecyclerView.addItemDecoration(itemDecoration)

        smallIconAdapter = SmallIconAdapter(smallIconData)
        binding.smallIconRecyclerView.adapter = smallIconAdapter
        smallIconLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.smallIconRecyclerView.layoutManager = smallIconLayoutManager
        binding.smallIconRecyclerView.setOnTouchListener { _, _ ->
            true
        }
        val smallIconItemDecoration = SmallIconItemOffsetDecoration(30)
        binding.smallIconRecyclerView.addItemDecoration(smallIconItemDecoration)
        binding.smallIconRecyclerView.scrollToPosition(smallIconAdapter.itemCount - 5)




        bigIconTimer = Timer()


        smallIconTimer = Timer()


        binding.emailLoginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_accountMainFragment_to_accountEmailSignInFragment)
        }
        binding.emailLoginLayout.setOnClickListener {
            findNavController().navigate(R.id.action_accountMainFragment_to_accountEmailSignInFragment)
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

    }

    override fun onPause() {
        super.onPause()
        bigIconTimer.cancel()
        smallIconTimer.cancel()
    }

    override fun onResume() {
        super.onResume()
        bigIconTimer.cancel()
        bigIconTimer = Timer()
        bigIconTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 2) {
                        // 마지막 아이템에 도달하면 첫 번째 아이템으로 스크롤
                        binding.bigIconRecyclerView.scrollToPosition(1)
                    } else {
                        binding.bigIconRecyclerView.scrollBy(1, 0) // 50 픽셀만큼 오른쪽으로 스크롤
                    }
                }
            }
        }, 0, 10) // 100ms 마다 스크롤 동작 실행

        smallIconTimer.cancel()
        smallIconTimer = Timer()
        binding.smallIconRecyclerView.scrollToPosition(smallIconAdapter.itemCount - 5)
        smallIconTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    if (smallIconLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        // 마지막 아이템에 도달하면 첫 번째 아이템으로 스크롤
                        binding.smallIconRecyclerView.scrollToPosition(smallIconAdapter.itemCount - 1)

                    } else {
                        binding.smallIconRecyclerView.scrollBy(-1, 0) // 50 픽셀만큼 오른쪽으로 스크롤
                    }
                }
            }
        }, 0, 10) // 100ms 마다 스크롤 동작 실행
    }

}





