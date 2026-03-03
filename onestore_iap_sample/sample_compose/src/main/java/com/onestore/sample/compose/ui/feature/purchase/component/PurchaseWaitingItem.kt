import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.gaa.sdk.iap.ProductDetail
import com.gaa.sdk.iap.PurchaseClient.ProductType
import com.gaa.sdk.iap.PurchaseData
import com.onestore.sample.compose.R
import com.onestore.sample.compose.ui.component.AppStatusBadge
import com.onestore.sample.compose.ui.component.ProductInfoListItem

/**
 * 개별 구매 대기 항목을 표시하고 처리(소비/승인)를 수행하는 리스트 아이템 컴포넌트입니다.
 * 내부적으로 ProductInfoListItem을 사용하여 통일된 스타일을 제공합니다.
 *
 * @param purchase 구매 데이터 정보
 * @param product 상품 상세 정보 (상세 정보가 없을 수 있으므로 nullable)
 * @param productType 상품 타입 (인앱, 월정액 등)
 * @param onHandlePurchase 처리 버튼 클릭 시 호출되는 콜백
 */
@Composable
fun PurchaseWaitingItem(
    purchase: PurchaseData,
    product: ProductDetail?,
    productType: String,
    onHandlePurchase: () -> Unit
) {
    var showAlert by remember { mutableStateOf(false) }

    val actionLabel = when (productType) {
        ProductType.AUTO, ProductType.SUBS -> stringResource(R.string.purchase_acknowledge)
        ProductType.INAPP -> stringResource(R.string.purchase_consume)
        else -> stringResource(R.string.unknown)
    }

    if (showAlert && product != null) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 24.dp),
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            onDismissRequest = {
                showAlert = false
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(text = stringResource(R.string.purchase_waiting_title))
            },
            text = {
                Column {
                    Text(
                        text = product.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.purchase_waiting_confirm_message),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAlert = false
                        onHandlePurchase()
                    }
                ) {
                    Text(text = actionLabel)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAlert = false
                    }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    ProductInfoListItem(
        title = product?.title ?: stringResource(R.string.product_detail_unknown),
        productId = purchase.productId,
        price = product?.price,
        priceCurrencyCode = product?.priceCurrencyCode,
        trailingContent = {
            AppStatusBadge(
                icon = Icons.Rounded.CheckCircle,
                text = actionLabel,
                onClick = { showAlert = true },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        },
        onClick = { showAlert = true }
    )
}