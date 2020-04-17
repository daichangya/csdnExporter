package run.halo.app.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import run.halo.app.Application;
import run.halo.app.model.entity.*;
import run.halo.app.model.enums.PostEditorType;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.repository.base.BasePostRepository;
import run.halo.app.service.impl.PostServiceImpl;
import run.halo.app.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Page repository test.
 *
 * @author johnniang
 * @date 3/22/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
public class PostRepositoryTest {

    @Autowired
    private BasePostRepository<Post> basePostRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private  PostCategoryRepository postCategoryRepository;

    @Autowired
    private  PostTagRepository postTagRepository;

    @Test
    public void listAllTest() {
        List<Post> posts = basePostRepository.findAll();
        log.debug("{}", posts);
    }


    @Test
    public void readTxt() throws IOException {
        String directory = this.getClass().getResource("/articles").getPath();
        Collection<File> listFiles = FileUtils.listFiles(new File(directory), FileFilterUtils.suffixFileFilter("txt"), null);
        Date now = new Date();
        basePostRepository.deleteAll();
        for (File file : listFiles) {
            CsdnBean csdnBean = JsonUtils.jsonToObject(FileUtils.readFileToString(file),CsdnBean.class);
//            System.out.println(csdnBean.getCategories());
//            System.out.println("tags: "+csdnBean.getTags());
            Post post = new Post();
            post.setFormatContent(csdnBean.getContent());
            post.setOriginalContent(csdnBean.getMarkdowncontent());
            post.setStatus(PostStatus.PUBLISHED);
            post.setEditTime(now);
            post.setSummary(csdnBean.getDescription());
            post.setTitle(csdnBean.getTitle());
//            post.setVisits(RandomUtils.nextLong(0,1000));
            post.setCreateTime(now);
            post.setUpdateTime(now);
            post.setEditorType(PostEditorType.RICHTEXT);
            post.setSlug(csdnBean.getArticle_id());
            post.setMetaKeywords("novelly,"+csdnBean.getDescription());
            post = basePostRepository.save(post);

            if(StringUtils.isNotEmpty(csdnBean.getCategories())){
                List<PostCategory> postCategoryList = Lists.newArrayList();
                for(String categorie :csdnBean.getCategories().split(",")){
                    if(categoryRepository.countByName(categorie)<=0){
                        Category category = new Category();
                        category.setName(categorie);
                        category.setSlug(categorie);
                        category.setParentId(0);
                        categoryRepository.save(category);
                    }
                    Optional<Category> categoryOptional = categoryRepository.getByName(categorie);
                    if(categoryOptional.isPresent()){
                        PostCategory postCategory = new PostCategory();
                        postCategory.setPostId(post.getId());
                        postCategory.setCategoryId(categoryOptional.get().getId());
                        postCategory.setCreateTime(now);
                        postCategory.setUpdateTime(now);
                        postCategoryList.add(postCategory);
                    }
                }
                postCategoryRepository.saveAll(postCategoryList);
            }
            if(StringUtils.isNotEmpty(csdnBean.getTags())){
                List<PostTag> postTagList = Lists.newArrayList();
                for(String tag :csdnBean.getTags().split(",")){
                    if(tagRepository.countByNameOrSlug(tag,tag)<=0){
                        Tag tagBean = new Tag();
                        tagBean.setName(tag);
                        tagBean.setSlug(tag);
                        tagRepository.save(tagBean);
                    }
                    Optional<Tag> tagOptional = tagRepository.getByName(tag);
                    if(tagOptional.isPresent()){
                        PostTag postTag = new PostTag();
                        postTag.setPostId(post.getId());
                        postTag.setTagId(tagOptional.get().getId());
                        postTag.setCreateTime(now);
                        postTag.setUpdateTime(now);
                        postTagList.add(postTag);
                    }
                }
                postTagRepository.saveAll(postTagList);
            }
        }
        System.out.println(basePostRepository.findAll().size());
    }
}
