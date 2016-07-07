package alexiuscrow;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.java.PsiAnnotationImpl;
import com.intellij.psi.impl.source.tree.java.PsiNameValuePairImpl;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.intellij.psi.impl.source.tree.JavaElementType.ANNOTATION_ARRAY_INITIALIZER;

public class SwaggerExtPropFoldingBuilder extends FoldingBuilderEx {
    private final static String FOLDER_PLACEHOLDER = "Swagger extension properties";
    private final static String SWAGGER_FOLDING_GROUP_NAME = "swagger-ext-prop";
    private final static String EXPECTED_ANNOTATION_PROPERTY_NAME = "properties";
    private final static String EXPECTED_ANNOTATION_NAME = "io.swagger.annotations.Extension";
    private final boolean collapsedByDefault = true;

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        FoldingGroup group = FoldingGroup.newGroup(SWAGGER_FOLDING_GROUP_NAME);
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        Collection<PsiAnnotationMemberValue> expressions = PsiTreeUtil.findChildrenOfType(root, PsiAnnotationMemberValue.class);
        for (final PsiAnnotationMemberValue expression: expressions) {
            ASTNode node = expression.getNode();
            if (node.getElementType().equals(ANNOTATION_ARRAY_INITIALIZER)) {
                if (expression.getParent() instanceof PsiNameValuePairImpl && expression.getParent().getParent().getParent() instanceof PsiAnnotationImpl) {
                    PsiNameValuePairImpl annPropName = (PsiNameValuePairImpl) expression.getParent();
                    PsiAnnotationImpl annName = (PsiAnnotationImpl) expression.getParent().getParent().getParent();
                    if (EXPECTED_ANNOTATION_PROPERTY_NAME.equals(annPropName.getName()) && EXPECTED_ANNOTATION_NAME.equals(annName.getQualifiedName())) {
                        TextRange textRange = new TextRange(expression.getTextRange().getStartOffset() + 1, expression.getTextRange().getEndOffset() - 1);
                        FoldingDescriptor descriptor = new FoldingDescriptor(node, textRange, group);
                        descriptors.add(descriptor);
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
        return collapsedByDefault;
    }
}
