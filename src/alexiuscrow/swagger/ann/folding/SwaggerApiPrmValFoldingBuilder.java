package alexiuscrow.swagger.ann.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.intellij.psi.util.PsiTreeUtil.findChildrenOfType;

public class SwaggerApiPrmValFoldingBuilder extends FoldingBuilderEx {
    private final static String FOLDER_PLACEHOLDER = "...";
    private final static String SWAGGER_FOLDING_GROUP_BASE_NAME = "swagger-api-param-long-val";
    private final static String EXPECTED_ANNOTATION_PROPERTY_NAME = "value";
    private final static String EXPECTED_ANNOTATION_NAME = "io.swagger.annotations.ApiParam";
    private final static boolean COLLAPSED_BY_DEFAULT = true;
    private final static int NORMAL_TEXT_LENGTH = 80;

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        Collection<PsiAnnotation> annotations = findChildrenOfType(root, PsiAnnotation.class);
        long groupIterationId = 0;
        for (PsiAnnotation annotation : annotations) {
            if (EXPECTED_ANNOTATION_NAME.equals(annotation.getQualifiedName())) {
                Collection<PsiNameValuePair> nameValuePairs = PsiTreeUtil.findChildrenOfType(annotation.getOriginalElement(), PsiNameValuePair.class);
                for (PsiNameValuePair pair : nameValuePairs) {
                    if (EXPECTED_ANNOTATION_PROPERTY_NAME.equals(pair.getName())) {
                        PsiAnnotation parentAnnotation = PsiTreeUtil.getParentOfType(pair.getOriginalElement(), PsiAnnotation.class);
                        if (parentAnnotation != null && EXPECTED_ANNOTATION_NAME.equals(parentAnnotation.getQualifiedName())) {
                            PsiElement element = PsiTreeUtil.getChildOfAnyType(pair.getOriginalElement(), PsiPolyadicExpression.class, PsiLiteralExpression.class);
                            if (element != null && element.getTextLength() > NORMAL_TEXT_LENGTH + 1) {
                                TextRange textRange = new TextRange(element.getTextRange().getStartOffset() + NORMAL_TEXT_LENGTH + 1, element.getTextRange().getEndOffset() - 1);
                                FoldingGroup group = FoldingGroup.newGroup(String.format("%s-%d", SWAGGER_FOLDING_GROUP_BASE_NAME, groupIterationId++));
                                FoldingDescriptor descriptor = new FoldingDescriptor(element.getNode(), textRange, group);
                                descriptors.add(descriptor);
                            }
                        }
                    }
                }
            }
        }
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        return FOLDER_PLACEHOLDER;
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return COLLAPSED_BY_DEFAULT;
    }
}
