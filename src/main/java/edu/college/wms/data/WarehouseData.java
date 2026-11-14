package edu.college.wms.data;

import edu.college.wms.model.Product;
import edu.college.wms.model.StockMovement;
import edu.college.wms.model.Supplier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Shared, in-memory sample data used by the Swing screens. */
public class WarehouseData {
    private final List<Product> products = new ArrayList<>();
    private final List<Supplier> suppliers = new ArrayList<>();
    private final List<StockMovement> movements = new ArrayList<>();
    private int nextProductId = 1001;
    private int nextSupplierId = 501;

    public WarehouseData() {
        addSeedProduct("Packing Tape", "Packaging", "3.50", 82, "Clear 48 mm packing tape", "Standard", 15);
        addSeedProduct("Safety Gloves", "Safety", "8.25", 9, "General purpose work gloves", "Standard", 12);
        addSeedProduct("Shipping Box - Medium", "Packaging", "1.80", 146, "Double-wall corrugated box", "Standard", 25);
        addSeedProduct("First Aid Kit", "Safety", "24.99", 18, "Workplace first aid kit", "Standard", 8);
        addSeedProduct("Thermal Labels", "Office", "12.00", 7, "Roll of 500 shipping labels", "Standard", 10);
        addSeedProduct("Cold Pack", "Medical", "2.15", 34, "Reusable cold pack", "Perishable", 10);
        addSeedProduct("Barcode Scanner", "Electronics", "89.00", 12, "Handheld USB barcode scanner", "Standard", 5);
        addSeedProduct("Stretch Wrap", "Packaging", "15.75", 4, "Pallet stretch film, 500 mm", "Standard", 8);

        suppliers.add(new Supplier(nextSupplierId++, "Asha Mehta", "+91 98765 43210", "asha@packright.example", "PackRight Supplies", "Pune, Maharashtra"));
        suppliers.add(new Supplier(nextSupplierId++, "Rahul Nair", "+91 98220 55667", "rahul@safepro.example", "SafePro Industrial", "Mumbai, Maharashtra"));
        suppliers.add(new Supplier(nextSupplierId++, "Mina Shah", "+91 97654 11223", "mina@labelworks.example", "LabelWorks India", "Ahmedabad, Gujarat"));
        suppliers.add(new Supplier(nextSupplierId++, "Dev Kapoor", "+91 98900 77112", "dev@medistock.example", "MediStock Distributors", "Nashik, Maharashtra"));
        suppliers.add(new Supplier(nextSupplierId++, "Neha Rao", "+91 98111 22334", "neha@techcart.example", "TechCart Warehouse", "Bengaluru, Karnataka"));

        LocalDateTime now = LocalDateTime.now();
        movements.add(new StockMovement(1001, "Packing Tape", 20, 82, "Sample receiving", now.minusMinutes(47)));
        movements.add(new StockMovement(1002, "Safety Gloves", -3, 9, "Sample issue", now.minusMinutes(28)));
        movements.add(new StockMovement(1005, "Thermal Labels", -3, 7, "Sample issue", now.minusMinutes(12)));
    }

    private void addSeedProduct(String name, String category, String price, int quantity,
                                String description, String type, int threshold) {
        products.add(new Product(nextProductId++, name, category, new BigDecimal(price), quantity,
                description, type, true, true, threshold));
    }

    public List<Product> getProducts() { return new ArrayList<>(products); }
    public List<Supplier> getSuppliers() { return new ArrayList<>(suppliers); }
    public List<StockMovement> getMovements() { return new ArrayList<>(movements); }
    public Optional<Product> findProduct(int id) { return products.stream().filter(p -> p.getId() == id).findFirst(); }
    public Optional<Supplier> findSupplier(int id) { return suppliers.stream().filter(s -> s.getId() == id).findFirst(); }

    public Product addProduct(Product product) {
        product.setId(nextProductId++);
        products.add(product);
        return product;
    }

    public void updateProduct(Product updated) {
        Product old = findProduct(updated.getId()).orElseThrow(() -> new IllegalArgumentException("Product not found."));
        if (old.getQuantity() != updated.getQuantity()) {
            movements.add(new StockMovement(old.getId(), old.getName(), updated.getQuantity() - old.getQuantity(),
                    updated.getQuantity(), "Product details updated", LocalDateTime.now()));
        }
        int index = products.indexOf(old);
        products.set(index, updated);
    }

    public void deleteProduct(int id) {
        products.removeIf(product -> product.getId() == id);
    }

    public void addSupplier(Supplier supplier) {
        supplier.setId(nextSupplierId++);
        suppliers.add(supplier);
    }

    public void updateSupplier(Supplier updated) {
        Supplier old = findSupplier(updated.getId()).orElseThrow(() -> new IllegalArgumentException("Supplier not found."));
        suppliers.set(suppliers.indexOf(old), updated);
    }

    public void deleteSupplier(int id) {
        suppliers.removeIf(supplier -> supplier.getId() == id);
    }

    public void adjustStock(int productId, int signedChange, String note) {
        Product product = findProduct(productId).orElseThrow(() -> new IllegalArgumentException("Product not found."));
        int updatedQuantity = product.getQuantity() + signedChange;
        if (updatedQuantity < 0) throw new IllegalArgumentException("There is not enough stock to issue that quantity.");
        if (signedChange == 0) throw new IllegalArgumentException("Enter a quantity greater than zero.");
        product.setQuantity(updatedQuantity);
        movements.add(new StockMovement(product.getId(), product.getName(), signedChange,
                updatedQuantity, note, LocalDateTime.now()));
    }
}
