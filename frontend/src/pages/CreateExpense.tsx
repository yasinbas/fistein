import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { ArrowLeft, Receipt, Save, Users, Calculator, DollarSign } from 'lucide-react';
import { expensesAPI, groupsAPI } from '../services/api';
import { useAuth } from '../utils/useAuth';
import type { Group, CreateExpenseRequest } from '../types';

const CreateExpense: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { user } = useAuth();
  const [groups, setGroups] = useState<Group[]>([]);
  const [selectedGroup, setSelectedGroup] = useState<Group | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string>('');
  
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    amount: '',
    currency: 'TRY',
    groupId: '',
  });

  const [splitMethod, setSplitMethod] = useState<'equal' | 'custom'>('equal');
  const [memberAmounts, setMemberAmounts] = useState<{ [userId: number]: number }>({});

  useEffect(() => {
    const loadGroups = async () => {
      try {
        const groupsData = await groupsAPI.getGroups();
        setGroups(groupsData);
        
        // Check if groupId is provided in URL params
        const groupIdParam = searchParams.get('groupId');
        if (groupIdParam) {
          const group = groupsData.find(g => g.id.toString() === groupIdParam);
          if (group) {
            setSelectedGroup(group);
            setFormData(prev => ({ ...prev, groupId: group.id.toString() }));
          }
        }
      } catch (error) {
        console.error('Gruplar yüklenirken hata:', error);
      } finally {
        setLoading(false);
      }
    };

    loadGroups();
  }, [searchParams]);

  useEffect(() => {
    if (selectedGroup && formData.amount) {
      const amount = parseFloat(formData.amount);
      if (!isNaN(amount) && splitMethod === 'equal') {
        const membersCount = selectedGroup.members?.length || 0;
        const equalShare = membersCount > 0 ? amount / membersCount : 0;
        
        const newMemberAmounts: { [userId: number]: number } = {};
        selectedGroup.members?.forEach(member => {
          newMemberAmounts[member.user.id] = equalShare;
        });
        setMemberAmounts(newMemberAmounts);
      }
    }
  }, [selectedGroup, formData.amount, splitMethod]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    if (name === 'groupId') {
      const group = groups.find(g => g.id.toString() === value);
      setSelectedGroup(group || null);
    }
    
    if (error) setError('');
  };

  const handleMemberAmountChange = (userId: number, amount: string) => {
    const numAmount = parseFloat(amount) || 0;
    setMemberAmounts(prev => ({
      ...prev,
      [userId]: numAmount
    }));
  };

  const getTotalMemberAmounts = () => {
    return Object.values(memberAmounts).reduce((sum, amount) => sum + amount, 0);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.title.trim()) {
      setError('Harcama başlığı gereklidir');
      return;
    }
    
    if (!formData.amount || isNaN(parseFloat(formData.amount))) {
      setError('Geçerli bir tutar giriniz');
      return;
    }
    
    if (!formData.groupId) {
      setError('Grup seçimi gereklidir');
      return;
    }

    const totalAmount = parseFloat(formData.amount);
    const totalMemberAmounts = getTotalMemberAmounts();
    
    if (Math.abs(totalAmount - totalMemberAmounts) > 0.01) {
      setError('Paylaşılan tutarların toplamı harcama tutarına eşit olmalıdır');
      return;
    }

    setSubmitting(true);
    setError('');

    try {
      const expenseData: CreateExpenseRequest = {
        title: formData.title.trim(),
        description: formData.description.trim() || undefined,
        amount: totalAmount,
        currency: formData.currency,
        groupId: parseInt(formData.groupId),
        memberAmounts: memberAmounts,
      };

      const newExpense = await expensesAPI.createExpense(expenseData);
      navigate(`/groups/${formData.groupId}`);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Harcama eklenirken bir hata oluştu');
    } finally {
      setSubmitting(false);
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY',
    }).format(amount);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex items-center gap-4">
          <button
            onClick={() => navigate(-1)}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <ArrowLeft className="h-5 w-5 text-gray-600" />
          </button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Yeni Harcama Ekle</h1>
            <p className="text-gray-600 mt-1">
              Grubunuzla paylaşacağınız harcamayı ekleyin
            </p>
          </div>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Basic Information */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Harcama Bilgileri</h2>
          
          {error && (
            <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-4">
              <p className="text-red-700">{error}</p>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-2">
                Harcama Başlığı *
              </label>
              <input
                type="text"
                id="title"
                name="title"
                value={formData.title}
                onChange={handleInputChange}
                placeholder="Örn: Market alışverişi, Akşam yemeği"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                required
              />
            </div>

            <div>
              <label htmlFor="amount" className="block text-sm font-medium text-gray-700 mb-2">
                Tutar *
              </label>
              <div className="relative">
                <DollarSign className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                <input
                  type="number"
                  id="amount"
                  name="amount"
                  value={formData.amount}
                  onChange={handleInputChange}
                  placeholder="0.00"
                  step="0.01"
                  min="0"
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  required
                />
              </div>
            </div>

            <div>
              <label htmlFor="groupId" className="block text-sm font-medium text-gray-700 mb-2">
                Grup *
              </label>
              <select
                id="groupId"
                name="groupId"
                value={formData.groupId}
                onChange={handleInputChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                required
              >
                <option value="">Grup seçiniz</option>
                {groups.map(group => (
                  <option key={group.id} value={group.id.toString()}>
                    {group.name}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label htmlFor="currency" className="block text-sm font-medium text-gray-700 mb-2">
                Para Birimi
              </label>
              <select
                id="currency"
                name="currency"
                value={formData.currency}
                onChange={handleInputChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="TRY">TRY (Türk Lirası)</option>
                <option value="USD">USD (US Dollar)</option>
                <option value="EUR">EUR (Euro)</option>
              </select>
            </div>
          </div>

          <div className="mt-6">
            <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
              Açıklama (İsteğe bağlı)
            </label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              placeholder="Harcama hakkında detaylar..."
              rows={3}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>
        </div>

        {/* Split Settings */}
        {selectedGroup && (
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Paylaşım Ayarları</h2>
            
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-3">
                Paylaşım Yöntemi
              </label>
              <div className="flex gap-4">
                <label className="flex items-center">
                  <input
                    type="radio"
                    value="equal"
                    checked={splitMethod === 'equal'}
                    onChange={(e) => setSplitMethod(e.target.value as 'equal' | 'custom')}
                    className="mr-2"
                  />
                  Eşit Paylaşım
                </label>
                <label className="flex items-center">
                  <input
                    type="radio"
                    value="custom"
                    checked={splitMethod === 'custom'}
                    onChange={(e) => setSplitMethod(e.target.value as 'equal' | 'custom')}
                    className="mr-2"
                  />
                  Özel Paylaşım
                </label>
              </div>
            </div>

            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <h3 className="font-medium text-gray-900">Grup Üyeleri</h3>
                <div className="text-sm text-gray-600">
                  Toplam: {formatCurrency(getTotalMemberAmounts())} / {formatCurrency(parseFloat(formData.amount) || 0)}
                </div>
              </div>

              {selectedGroup.members?.map(member => (
                <div key={member.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div className="flex items-center">
                    <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center mr-3">
                      <span className="text-sm font-medium text-primary-700">
                        {member.user.fullName.charAt(0)}
                      </span>
                    </div>
                    <div>
                      <p className="font-medium text-gray-900">{member.user.fullName}</p>
                      <p className="text-sm text-gray-600">{member.user.email}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-sm text-gray-600">₺</span>
                    <input
                      type="number"
                      value={memberAmounts[member.user.id] || 0}
                      onChange={(e) => handleMemberAmountChange(member.user.id, e.target.value)}
                      step="0.01"
                      min="0"
                      disabled={splitMethod === 'equal'}
                      className="w-20 px-2 py-1 border border-gray-300 rounded text-right disabled:bg-gray-100"
                    />
                  </div>
                </div>
              ))}
            </div>

            {Math.abs((parseFloat(formData.amount) || 0) - getTotalMemberAmounts()) > 0.01 && (
              <div className="mt-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                <p className="text-yellow-800 text-sm">
                  Uyarı: Paylaşılan tutarların toplamı ({formatCurrency(getTotalMemberAmounts())}) 
                  harcama tutarına ({formatCurrency(parseFloat(formData.amount) || 0)}) eşit değil.
                </p>
              </div>
            )}
          </div>
        )}

        {/* Action Buttons */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex gap-4">
            <button
              type="button"
              onClick={() => navigate(-1)}
              className="flex-1 btn btn-secondary"
              disabled={submitting}
            >
              İptal
            </button>
            <button
              type="submit"
              className="flex-1 btn btn-primary flex items-center justify-center gap-2"
              disabled={submitting || !selectedGroup}
            >
              {submitting ? (
                <>
                  <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                  Kaydediliyor...
                </>
              ) : (
                <>
                  <Save className="h-5 w-5" />
                  Harcama Ekle
                </>
              )}
            </button>
          </div>
        </div>
      </form>

      {/* Help Section */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">
          Paylaşım Nasıl Çalışır?
        </h3>
        <div className="space-y-3">
          <div className="flex items-start">
            <Calculator className="h-5 w-5 text-primary-600 mr-2 mt-0.5" />
            <div>
              <h4 className="font-medium text-gray-900">Eşit Paylaşım</h4>
              <p className="text-sm text-gray-600">
                Harcama tutarı grup üyeleri arasında eşit olarak paylaşılır
              </p>
            </div>
          </div>
          <div className="flex items-start">
            <Users className="h-5 w-5 text-primary-600 mr-2 mt-0.5" />
            <div>
              <h4 className="font-medium text-gray-900">Özel Paylaşım</h4>
              <p className="text-sm text-gray-600">
                Her üye için farklı tutarlar belirleyebilirsiniz
              </p>
            </div>
          </div>
          <div className="flex items-start">
            <Receipt className="h-5 w-5 text-primary-600 mr-2 mt-0.5" />
            <div>
              <h4 className="font-medium text-gray-900">Otomatik Hesaplama</h4>
              <p className="text-sm text-gray-600">
                Kim kime ne kadar borçlu olduğu otomatik hesaplanır
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreateExpense;