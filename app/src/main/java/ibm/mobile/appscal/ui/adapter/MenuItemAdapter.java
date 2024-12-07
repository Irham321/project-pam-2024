package ibm.mobile.appscal.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ibm.mobile.appscal.data.model.MenuItem;
import ibm.mobile.appscal.databinding.MenuItemBinding;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder> {

    private List<MenuItem> menuItemList;
    private Context context;
    private MenuItemCallback callback;

    public interface MenuItemCallback {
        void onGramInput(int gram, MenuItem menuItem);
    }

    public MenuItemAdapter(List<MenuItem> menuItemList, MenuItemCallback callback) {
        this.menuItemList = menuItemList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        MenuItemBinding binding = MenuItemBinding.inflate(inflater, parent, false);
        return new MenuItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        MenuItem menuItem = menuItemList.get(position);
        holder.bind(menuItem);
    }

    @Override
    public int getItemCount() {
        return menuItemList.size();
    }

    class MenuItemViewHolder extends RecyclerView.ViewHolder {

        private MenuItemBinding binding;

        public MenuItemViewHolder(MenuItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(MenuItem menuItem) {
            binding.foodNameTextView.setText(menuItem.getNamaMenu());
            binding.foodInfoTextView.setText(menuItem.getKalori() + " kkal | " + menuItem.getPerGram() + " gram");
            Glide.with(context).load(menuItem.getLinkGambar()).into(binding.foodImageView);
            binding.foodNameTextViewBig.setText(menuItem.getNamaMenu());
            binding.foodInfoTextViewBig.setText(menuItem.getKalori() + " kkal | " + menuItem.getPerGram() + " gram");
            Glide.with(context).load(menuItem.getLinkGambar()).into(binding.foodImageViewBig);

            binding.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.layoutItemSmall.setVisibility(View.GONE);
                    binding.layoutItemBig.setVisibility(View.VISIBLE);

                }
            });

            binding.btnToSmall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.layoutItemSmall.setVisibility(View.VISIBLE);
                    binding.layoutItemBig.setVisibility(View.GONE);
                }
            });

            binding.btnSelanjutnya.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String inputText = binding.etGramBerat.getText().toString();
                    if (!inputText.isEmpty()) {
                        int gram = Integer.parseInt(inputText);
                        callback.onGramInput(gram, menuItem);
                    }else{
                        Toast.makeText(context, "gram cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
