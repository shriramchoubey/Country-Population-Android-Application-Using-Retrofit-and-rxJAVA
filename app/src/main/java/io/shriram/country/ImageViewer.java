package io.shriram.country;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

public class ImageViewer extends AppCompatActivity {
    private String country,flag;

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialising Fresco
        Fresco.initialize(this);
        setContentView(R.layout.activity_image_viewer);

        //backButton over actionbar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        country = getIntent().getStringExtra("country");
        flag = getIntent().getStringExtra("flag");
        actionBar.setTitle(country);
        SimpleDraweeView img = (SimpleDraweeView) findViewById(R.id.iv_country);

        //setting the image URI
        Uri imageUri = Uri.parse(flag);
        Drawable myIcon = ImageViewer.this.getResources().getDrawable( R.drawable.placeholder );

        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(ImageViewer.this.getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);;
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setPlaceholderImage(myIcon)
                .build();
        img.setHierarchy(hierarchy);
        img.setImageURI(imageUri);
    }
}
