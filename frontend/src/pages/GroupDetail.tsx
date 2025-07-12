import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { 
  ArrowLeft, 
  Users, 
  Plus, 
  Settings, 
  Receipt, 
  TrendingUp, 
  TrendingDown, 
  UserPlus,
  Calendar,
  DollarSign
} from 'lucide-react';
import { groupsAPI, expensesAPI } from '../services/api';
import type { Group, GroupBalance, Expense } from '../types';

const GroupDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [group, setGroup] = useState<Group | null>(null);
  const [groupBalance, setGroupBalance] = useState<GroupBalance | null>(null);
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'overview' | 'members' | 'expenses'>('overview');
  const [showAddMember, setShowAddMember] = useState(false);
  const [newMemberEmail, setNewMemberEmail] = useState('');
  const [addMemberLoading, setAddMemberLoading] = useState(false);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    const loadGroupData = async () => {
      if (!id) return;
      
      try {
        const groupId = parseInt(id);
        const [groupData, balanceData, expensesData] = await Promise.all([
          groupsAPI.getGroup(groupId),
          groupsAPI.getGroupBalance(groupId),
          expensesAPI.getExpenses(), // This will need to be filtered by group
        ]);

        setGroup(groupData);
        setGroupBalance(balanceData);
        // Filter expenses for this group
        setExpenses(expensesData.filter(expense => expense.group.id === groupId));
      } catch (error) {
        console.error('Grup verileri yüklenirken hata:', error);
        setError('Grup verileri yüklenirken bir hata oluştu');
      } finally {
        setLoading(false);
      }
    };

    loadGroupData();
  }, [id]);

  const handleAddMember = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id || !newMemberEmail.trim()) return;

    setAddMemberLoading(true);
    setError('');

    try {
      await groupsAPI.addMember(parseInt(id), newMemberEmail.trim());
      setNewMemberEmail('');
      setShowAddMember(false);
      // Reload group data
      window.location.reload();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Üye eklenirken bir hata oluştu');
    } finally {
      setAddMemberLoading(false);
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY',
    }).format(amount);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('tr-TR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  };

  const getBalanceColorClass = (amount: number) => {
    if (amount > 0) return 'text-green-600';
    if (amount < 0) return 'text-red-600';
    return 'text-gray-600';
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  if (!group) {
    return (
      <div className="text-center py-12">
        <div className="text-red-600 text-lg">Grup bulunamadı</div>
        <button
          onClick={() => navigate(-1)}
          className="mt-4 btn btn-secondary"
        >
          Geri Dön
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <button
              onClick={() => navigate(-1)}
              className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            >
              <ArrowLeft className="h-5 w-5 text-gray-600" />
            </button>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">{group.name}</h1>
              <p className="text-gray-600 mt-1">
                {group.description || 'Açıklama bulunmuyor'}
              </p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Link
              to={`/expenses/new?groupId=${group.id}`}
              className="btn btn-primary flex items-center gap-2"
            >
              <Plus className="h-5 w-5" />
              Harcama Ekle
            </Link>
            <button className="btn btn-secondary flex items-center gap-2">
              <Settings className="h-5 w-5" />
              Ayarlar
            </button>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <div className="flex border-b">
          <button
            onClick={() => setActiveTab('overview')}
            className={`px-6 py-4 font-medium ${
              activeTab === 'overview' 
                ? 'border-b-2 border-primary-500 text-primary-600' 
                : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Genel Bakış
          </button>
          <button
            onClick={() => setActiveTab('members')}
            className={`px-6 py-4 font-medium ${
              activeTab === 'members' 
                ? 'border-b-2 border-primary-500 text-primary-600' 
                : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Üyeler ({group.members?.length || 0})
          </button>
          <button
            onClick={() => setActiveTab('expenses')}
            className={`px-6 py-4 font-medium ${
              activeTab === 'expenses' 
                ? 'border-b-2 border-primary-500 text-primary-600' 
                : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Harcamalar ({expenses.length})
          </button>
        </div>

        <div className="p-6">
          {/* Overview Tab */}
          {activeTab === 'overview' && (
            <div className="space-y-6">
              {/* Group Stats */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="bg-blue-50 p-6 rounded-lg">
                  <div className="flex items-center">
                    <Users className="h-8 w-8 text-blue-600 mr-3" />
                    <div>
                      <p className="text-sm text-blue-600">Toplam Üye</p>
                      <p className="text-2xl font-bold text-blue-900">
                        {group.members?.length || 0}
                      </p>
                    </div>
                  </div>
                </div>
                <div className="bg-green-50 p-6 rounded-lg">
                  <div className="flex items-center">
                    <Receipt className="h-8 w-8 text-green-600 mr-3" />
                    <div>
                      <p className="text-sm text-green-600">Toplam Harcama</p>
                      <p className="text-2xl font-bold text-green-900">
                        {expenses.length}
                      </p>
                    </div>
                  </div>
                </div>
                <div className="bg-yellow-50 p-6 rounded-lg">
                  <div className="flex items-center">
                    <DollarSign className="h-8 w-8 text-yellow-600 mr-3" />
                    <div>
                      <p className="text-sm text-yellow-600">Toplam Tutar</p>
                      <p className="text-2xl font-bold text-yellow-900">
                        {formatCurrency(expenses.reduce((sum, expense) => sum + expense.amount, 0))}
                      </p>
                    </div>
                  </div>
                </div>
              </div>

              {/* Balance Overview */}
              {groupBalance && (
                <div className="bg-gray-50 p-6 rounded-lg">
                  <h3 className="text-lg font-semibold text-gray-900 mb-4">Bakiye Durumu</h3>
                  <div className="space-y-3">
                    {groupBalance.balances.map((balance, index) => (
                      <div key={index} className="flex justify-between items-center p-3 bg-white rounded-lg">
                        <div className="flex items-center">
                          <div className="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center mr-3">
                            <span className="text-sm font-medium text-gray-700">
                              {balance.user.fullName.charAt(0)}
                            </span>
                          </div>
                          <span className="font-medium text-gray-900">
                            {balance.user.fullName}
                          </span>
                        </div>
                        <div className="flex items-center">
                          <span className={`text-lg font-semibold ${getBalanceColorClass(balance.amount)}`}>
                            {formatCurrency(balance.amount)}
                          </span>
                          {balance.amount > 0 && <TrendingUp className="h-5 w-5 text-green-500 ml-2" />}
                          {balance.amount < 0 && <TrendingDown className="h-5 w-5 text-red-500 ml-2" />}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Recent Expenses */}
              <div>
                <div className="flex justify-between items-center mb-4">
                  <h3 className="text-lg font-semibold text-gray-900">Son Harcamalar</h3>
                  <button
                    onClick={() => setActiveTab('expenses')}
                    className="text-primary-600 hover:text-primary-500 text-sm font-medium"
                  >
                    Tümünü Gör
                  </button>
                </div>
                <div className="space-y-3">
                  {expenses.slice(0, 3).map((expense) => (
                    <div key={expense.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                      <div className="flex items-center">
                        <Receipt className="h-5 w-5 text-gray-400 mr-3" />
                        <div>
                          <h4 className="font-medium text-gray-900">{expense.title}</h4>
                          <p className="text-sm text-gray-600">
                            {expense.paidBy.fullName} • {formatDate(expense.createdAt)}
                          </p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="font-semibold text-gray-900">
                          {formatCurrency(expense.amount)}
                        </p>
                      </div>
                    </div>
                  ))}
                  {expenses.length === 0 && (
                    <div className="text-center py-8 text-gray-500">
                      <Receipt className="h-12 w-12 mx-auto mb-4 text-gray-300" />
                      <p>Henüz harcama yok</p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* Members Tab */}
          {activeTab === 'members' && (
            <div className="space-y-6">
              <div className="flex justify-between items-center">
                <h3 className="text-lg font-semibold text-gray-900">Grup Üyeleri</h3>
                <button
                  onClick={() => setShowAddMember(true)}
                  className="btn btn-primary flex items-center gap-2"
                >
                  <UserPlus className="h-5 w-5" />
                  Üye Ekle
                </button>
              </div>

              {showAddMember && (
                <div className="bg-blue-50 p-6 rounded-lg">
                  <h4 className="font-medium text-blue-900 mb-4">Yeni Üye Ekle</h4>
                  <form onSubmit={handleAddMember} className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-blue-700 mb-2">
                        E-posta Adresi
                      </label>
                      <input
                        type="email"
                        value={newMemberEmail}
                        onChange={(e) => setNewMemberEmail(e.target.value)}
                        placeholder="ornek@email.com"
                        className="w-full px-3 py-2 border border-blue-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        required
                      />
                    </div>
                    {error && (
                      <div className="text-red-600 text-sm">{error}</div>
                    )}
                    <div className="flex gap-2">
                      <button
                        type="submit"
                        disabled={addMemberLoading}
                        className="btn btn-primary flex-1"
                      >
                        {addMemberLoading ? 'Ekleniyor...' : 'Ekle'}
                      </button>
                      <button
                        type="button"
                        onClick={() => setShowAddMember(false)}
                        className="btn btn-secondary flex-1"
                      >
                        İptal
                      </button>
                    </div>
                  </form>
                </div>
              )}

              <div className="space-y-3">
                {group.members?.map((member) => (
                  <div key={member.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                    <div className="flex items-center">
                      <div className="w-10 h-10 bg-primary-100 rounded-full flex items-center justify-center mr-3">
                        <span className="text-sm font-medium text-primary-700">
                          {member.user.fullName.charAt(0)}
                        </span>
                      </div>
                      <div>
                        <h4 className="font-medium text-gray-900">{member.user.fullName}</h4>
                        <p className="text-sm text-gray-600">{member.user.email}</p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className="text-sm text-gray-500">
                        {formatDate(member.joinedAt)}
                      </p>
                      {member.user.id === group.createdBy.id && (
                        <span className="text-xs bg-primary-100 text-primary-700 px-2 py-1 rounded-full">
                          Yönetici
                        </span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Expenses Tab */}
          {activeTab === 'expenses' && (
            <div className="space-y-6">
              <div className="flex justify-between items-center">
                <h3 className="text-lg font-semibold text-gray-900">Tüm Harcamalar</h3>
                <Link
                  to={`/expenses/new?groupId=${group.id}`}
                  className="btn btn-primary flex items-center gap-2"
                >
                  <Plus className="h-5 w-5" />
                  Harcama Ekle
                </Link>
              </div>

              <div className="space-y-3">
                {expenses.map((expense) => (
                  <div key={expense.id} className="p-4 bg-gray-50 rounded-lg">
                    <div className="flex justify-between items-start">
                      <div className="flex items-center">
                        <Receipt className="h-5 w-5 text-gray-400 mr-3" />
                        <div>
                          <h4 className="font-medium text-gray-900">{expense.title}</h4>
                          <p className="text-sm text-gray-600">
                            {expense.description || 'Açıklama yok'}
                          </p>
                          <p className="text-sm text-gray-500">
                            {expense.paidBy.fullName} tarafından ödendi • {formatDate(expense.createdAt)}
                          </p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="text-lg font-semibold text-gray-900">
                          {formatCurrency(expense.amount)}
                        </p>
                        <p className="text-sm text-gray-600">
                          {expense.expenseShares?.length || 0} kişi paylaştı
                        </p>
                      </div>
                    </div>
                  </div>
                ))}
                {expenses.length === 0 && (
                  <div className="text-center py-12 text-gray-500">
                    <Receipt className="h-16 w-16 mx-auto mb-4 text-gray-300" />
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">Henüz harcama yok</h3>
                    <p className="text-gray-600 mb-6">
                      Bu grupta henüz hiç harcama kaydı bulunmuyor.
                    </p>
                    <Link
                      to={`/expenses/new?groupId=${group.id}`}
                      className="btn btn-primary inline-flex items-center gap-2"
                    >
                      <Plus className="h-5 w-5" />
                      İlk Harcamayı Ekle
                    </Link>
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default GroupDetail;