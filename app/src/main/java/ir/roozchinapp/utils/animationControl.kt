package ir.roozchinapp.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize


@Composable
fun AnimationControlForDoc(
    modifier: Modifier = Modifier,
) {

    //متغییرهای زمان

    val animationSpecForFade: FiniteAnimationSpec<Float> = tween(durationMillis = 500)
    val animationSpecForExpand: FiniteAnimationSpec<IntSize> = tween(durationMillis = 500)




                                    //fade in_out

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = animationSpecForFade) + expandVertically(
            animationSpec = animationSpecForExpand
        ),
        exit = fadeOut(animationSpec = animationSpecForFade) + shrinkVertically(
            animationSpec = animationSpecForExpand
        )
    ) {

       // content
    }




                                                    //move from Bottom to top
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { it } // شروع از پایین
        ) + fadeIn(animationSpec = animationSpecForFade),
        exit = slideOutVertically(
            targetOffsetY = { it } // خروج به پایین
        ) + fadeOut(animationSpec = animationSpecForFade)
    ) {

        //content
    }





                                                   //move from top to bottom
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { -it }
        ) + fadeIn(animationSpec = animationSpecForFade),
        exit = slideOutVertically(
            targetOffsetY = { -it }
        ) + fadeOut(animationSpec = animationSpecForFade)
    ) {

        //content
    }





                                                     //move from left to Right
    AnimatedVisibility(
        visible = true,
        enter = slideInHorizontally(
            initialOffsetX = { -it } // شروع از چپ
        ) + fadeIn(animationSpec = animationSpecForFade),
        exit = slideOutHorizontally(
            targetOffsetX = { -it } // خروج به چپ
        ) + fadeOut(animationSpec = animationSpecForFade)
    ) {
        //content
    }




                                                               //move from  Right to left
    AnimatedVisibility(
        visible = true,
        enter = slideInHorizontally(
            initialOffsetX = { it }
        ) + fadeIn(animationSpec = animationSpecForFade),
        exit = slideOutHorizontally(
            targetOffsetX = { it }
        ) + fadeOut(animationSpec = animationSpecForFade)
    ) {
        //content
    }




}