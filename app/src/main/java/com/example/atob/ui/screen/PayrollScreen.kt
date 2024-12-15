package com.example.atob.ui.screen
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atob.model.Payroll
import com.example.atob.ui.state.PayrollViewUiState
import com.example.atob.ui.viewModel.PayrollViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayrollScreen(payrollViewModel: PayrollViewModel = viewModel(factory = PayrollViewModel.Factory)) {
    val uiState by payrollViewModel.uiState.collectAsState()
    var showPayRate by remember { mutableStateOf(false) } // State to toggle pay rate visibility

    LaunchedEffect(Unit) {
        payrollViewModel.fetchPayrolls()
    }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Payrolls", style = MaterialTheme.typography.titleMedium) }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (uiState) {
                    is PayrollViewUiState.Loading -> {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ){ CircularProgressIndicator() }
                    }

                    is PayrollViewUiState.Success -> {
                        val payrolls = (uiState as PayrollViewUiState.Success).payrolls
                        PayrollContent(
                            payrolls = payrolls,
                            showPayRate = showPayRate,
                            onTogglePayRate = { showPayRate = !showPayRate },
                            payRate = payrollViewModel.payRate
                        )
                    }

                    is PayrollViewUiState.Error -> {
                        Text(
                            text = (uiState as PayrollViewUiState.Error).message,
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

}
@Composable
fun PayrollContent(
    payrolls: List<Payroll>,
    showPayRate: Boolean,
    onTogglePayRate: () -> Unit,
    payRate : Double?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Toggle Pay Rate Visibility
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Show Pay Rate",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = showPayRate,
                onCheckedChange = { onTogglePayRate() }
            )
        }

        if (showPayRate) {
            Text(
                text = "Pay Rate: ${payRate ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // List of Payrolls
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(payrolls) { payroll ->
                PayrollCard(payroll)
            }
        }
    }
}
@Composable
fun PayrollCard(payroll: Payroll) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Date: ${payroll.date}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Shift Id: ${payroll.shift_id}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Total Hours: ${payroll.totalHoursWorked}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Total Pay: $${payroll.totalPay}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

