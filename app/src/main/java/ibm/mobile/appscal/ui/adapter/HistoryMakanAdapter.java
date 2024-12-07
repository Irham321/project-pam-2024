package ibm.mobile.appscal.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ibm.mobile.appscal.data.model.HistoryMakan;
import ibm.mobile.appscal.data.model.MenuItem;
import ibm.mobile.appscal.databinding.HistoryItemBinding;
import ibm.mobile.appscal.ui.DateUtil;

public class HistoryMakanAdapter extends RecyclerView.Adapter<HistoryMakanAdapter.HistoryMakanViewHolder> {

    private Context context;
    private List<HistoryMakan> historyMakanList = new ArrayList<>();
    private List<MenuItem> menuItemList = new ArrayList<>();
    private OnHistoryItemCallback callback;

    public interface OnHistoryItemCallback {
        void onSimpan(HistoryMakan historyMakan);
        void onHapus(HistoryMakan historyMakan);
    }

    public HistoryMakanAdapter(Context context, OnHistoryItemCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void submitHistoryList(List<HistoryMakan> historyMakanList) {
        this.historyMakanList = historyMakanList;
        notifyDataSetChanged();
    }

    public void submitMenuList(List<MenuItem> menuItemList) {
        this.menuItemList = menuItemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryMakanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HistoryItemBinding binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HistoryMakanViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryMakanViewHolder holder, int position) {
        HistoryMakan historyMakan = historyMakanList.get(position);
        holder.bind(historyMakan);
    }

    @Override
    public int getItemCount() {
        return historyMakanList.size();
    }

    class HistoryMakanViewHolder extends RecyclerView.ViewHolder {
        private HistoryItemBinding binding;

        public HistoryMakanViewHolder(@NonNull HistoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(HistoryMakan historyMakan) {
            String waktuMakan = historyMakan.getWaktuMakan() == 0? "Sarapan" : historyMakan.getWaktuMakan() == 1? "Lunch" : "Dinner";
            binding.tvHistoryWaktu.setText(DateUtil.formatTimestamp(historyMakan.getTimeStampHistory()) + " - "+ waktuMakan);
            binding.etBeratMakan.setText(String.valueOf(historyMakan.getGramMenu()));
            binding.btnEdit.setOnClickListener(v -> showEditOptions());

            MenuItem menuItem = getMenuItemById(historyMakan.getMenuId());
            if (menuItem != null) {
                binding.tvHistoryMakanan.setText(menuItem.getNamaMenu());
                float totalCalories = calculateCalories(historyMakan.getGramMenu(), ((float)menuItem.getKalori()/(float)menuItem.getPerGram()));
                Log.d("historyMakanFragment", "totalCalories " + totalCalories);
                binding.tvHistoryMakananInfo.setText(totalCalories + " kal | " + historyMakan.getGramMenu() + " gram");
                Glide.with(context).load(menuItem.getLinkGambar()).into(binding.ivHistory);
            }

            binding.btnSimpan.setOnClickListener(v -> {
                String newGramET = binding.etBeratMakan.getText().toString();
                if(newGramET.isEmpty()){
                    Toast.makeText(context, "Gram cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    int newGramtoInt = Integer.parseInt(newGramET);
                    if(newGramtoInt != historyMakan.getGramMenu()){
                        historyMakan.setGramMenu(newGramtoInt);
                        callback.onSimpan(historyMakan);
                    }
                }
//                hideEditOptions();
            });

            binding.btnDelete.setOnClickListener(v -> {
                callback.onHapus(historyMakan);
//                hideEditOptions();
            });

            binding.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideEditOptions();
                }
            });
        }

        private void showEditOptions() {
            binding.etBeratMakan.setVisibility(View.VISIBLE);
            binding.editLayout.setVisibility(View.VISIBLE);
            binding.btnEdit.setVisibility(View.GONE);
            binding.tvHistoryMakananInfo.setVisibility(View.GONE);
        }

        private void hideEditOptions() {
            binding.etBeratMakan.setVisibility(View.GONE);
            binding.editLayout.setVisibility(View.GONE);
            binding.btnEdit.setVisibility(View.VISIBLE);
            binding.tvHistoryMakananInfo.setVisibility(View.VISIBLE);
        }

        private MenuItem getMenuItemById(String menuId) {
            for (MenuItem menuItem : menuItemList) {
                if (menuItem.getId().equals(menuId)) {
                    return menuItem;
                }
            }
            return null;
        }

        private float calculateCalories(int gram, float caloriesPerGram) {
            Log.d("historyMakanFragment", "caloriesPerGram " + caloriesPerGram);
            return gram * caloriesPerGram;
        }
    }
}