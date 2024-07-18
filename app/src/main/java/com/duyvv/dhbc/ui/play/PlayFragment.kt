package com.duyvv.dhbc.ui.play

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.duyvv.dhbc.databinding.FragmentPlayBinding
import com.duyvv.dhbc.domain.Button
import com.duyvv.dhbc.domain.ButtonType
import com.duyvv.dhbc.domain.Question
import com.duyvv.dhbc.ui.play.adapter.AnswerButtonAdapter
import com.duyvv.dhbc.ui.play.adapter.SelectButtonAdapter
import com.duyvv.dhbc.ui.play.dialog.DialogListener
import com.duyvv.dhbc.ui.play.dialog.GameOverDialog
import com.duyvv.dhbc.ui.play.dialog.VictoryDialog
import com.duyvv.dhbc.utils.CORRECT_MESSAGE
import com.duyvv.dhbc.utils.INCORRECT_MESSAGE
import com.duyvv.firstlesson.base.BaseFragment
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.launch
import kotlin.random.Random

@SuppressLint("SetTextI18n")
class PlayFragment : BaseFragment<FragmentPlayBinding>(), DialogListener {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPlayBinding.inflate(inflater, container, false)

    private val viewModel: PlayViewModel by viewModels()

    private lateinit var selectButtonAdapter: SelectButtonAdapter

    private lateinit var answerButtonAdapter: AnswerButtonAdapter

    override fun init() = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        initQuestion()
    }

    private fun initQuestion() {
        viewModel.initQuestions()
        nextQuestion()
    }

    private fun setUp() {
        setUpRecyclerview()

        binding.tvNext.setOnClickListener {
            nextQuestion()
        }

        lifecycleScope.launch {
            viewModel.heart.collect {
                if (it <= 0) {
                    GameOverDialog(
                        requireContext(),
                        viewModel.score.value,
                        this@PlayFragment
                    ).show()
                }
                binding.tvHeart.text = it.toString()
            }
        }

        lifecycleScope.launch {
            viewModel.score.collect {
                binding.tvScore.text = it.toString()
            }
        }
    }

    private fun setUpRecyclerview() {
        selectButtonAdapter = SelectButtonAdapter(
            onItemClicked = {
                onSelectAnswer(it)
            }
        )
        binding.rcvButtonselect.apply {
            adapter = selectButtonAdapter
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                justifyContent = JustifyContent.CENTER
            }
        }

        answerButtonAdapter = AnswerButtonAdapter()
        binding.rcvButtonAnswer.apply {
            adapter = answerButtonAdapter
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                justifyContent = JustifyContent.CENTER
            }
        }
    }

    private fun onSelectAnswer(button: Button) {
        // add button in answer adapter
        answerButtonAdapter.apply {
            addAnswerChar(button.body)

            // check answer is full or not
            if (position == items.size) {
                if (checkAnswerCorrect()) {
                    showResult(true)
                    binding.tvMessage.text = CORRECT_MESSAGE
                    viewModel.addScore()
                } else {
                    showResult(true)
                    binding.tvMessage.text = INCORRECT_MESSAGE
                    viewModel.decreaseHeart()
                }
            }
        }
    }

    private fun restartGame() {
        viewModel.restartGame()
        resetUI()
        nextQuestion()
    }

    private fun nextQuestion() {
        resetUI()
        viewModel.getRandomQuestion().let { question ->
            if (question == null) {
                VictoryDialog(
                    requireContext(),
                    viewModel.score.value,
                    this@PlayFragment
                ).show()
                return@let
            }

            binding.imgQuestion.setImageResource(question.resourceImg)

            selectButtonAdapter.apply {
                setItems(generateSelectButtons(question))
                setQuestion(question)
            }

            answerButtonAdapter.apply {
                setItems(generateAnswerButtons(question))
                setQuestion(question)
            }
        }
    }

    private fun generateSelectButtons(question: Question): List<Button> {
        val char = "ABCDDEGHIKLMNOPQRSTUVXY"
        val list = mutableListOf<Button>()
        question.answer.forEach {
            list.add(Button(it.toString()))
        }
        (1..16 - question.answer.length).map {
            list.add(Button(char[Random.nextInt(char.length)].toString()))
        }
        return list.shuffled()
    }

    private fun generateAnswerButtons(question: Question): List<Button> {
        return (1..question.answer.length).map { Button() }
    }

    private fun resetUI() {
        showResult(false)
        binding.tvMessage.text = ""
        answerButtonAdapter.changeType(ButtonType.BASIC)
    }

    private fun showResult(isShow: Boolean) {
        binding.tvNext.visibility = if (isShow) VISIBLE else INVISIBLE
        binding.tvMessage.visibility = if (isShow) VISIBLE else INVISIBLE
    }

    override fun onRestartGame() {
        restartGame()
    }

    override fun onExitGame() {
        activity?.finish()
    }
}